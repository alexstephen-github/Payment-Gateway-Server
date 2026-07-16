package com.paygateway.mock.support;

import com.paygateway.mock.model.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builds spec-shaped model objects populated with random data. Where a request body
 * carries meaningful values (amount, currency, customer_id, ...) those are echoed back
 * so the generated responses stay internally consistent.
 */
@Component
public class MockFactory {

    /* ------------------------------------------------------------------ */
    /* Shared / nested builders                                            */
    /* ------------------------------------------------------------------ */

    public Address address() {
        Address a = new Address();
        a.line1 = Rand.streetLine();
        a.city = Rand.city();
        a.state = Rand.pick("CA", "NY", "TX", "WA", "IL");
        a.postalCode = Rand.postalCode();
        a.country = Rand.country();
        return a;
    }

    public BillingDetails billingDetails() {
        BillingDetails b = new BillingDetails();
        b.name = Rand.fullName();
        b.email = Rand.email();
        b.phone = Rand.phone();
        b.address = address();
        return b;
    }

    public CardDetails card() {
        CardDetails c = new CardDetails();
        c.brand = Rand.cardBrand();
        c.last4 = Rand.last4();
        c.expMonth = Rand.expMonth();
        c.expYear = Rand.expYear();
        c.fingerprint = Rand.id("fp").substring(0, 20);
        c.funding = Rand.cardFunding();
        c.country = Rand.country();
        return c;
    }

    public BankAccountDetails bankAccount() {
        BankAccountDetails b = new BankAccountDetails();
        b.bankName = Rand.bankName();
        b.routingNumber = Rand.digits(9);
        b.last4 = Rand.last4();
        b.accountType = Rand.pick("checking", "savings");
        return b;
    }

    private ListMeta listMeta(int count) {
        ListMeta m = new ListMeta();
        m.hasMore = Rand.bool();
        m.totalCount = count + Rand.intRange(0, 100);
        m.url = "/list";
        return m;
    }

    /* ------------------------------------------------------------------ */
    /* Payments                                                            */
    /* ------------------------------------------------------------------ */

    public Payment payment(Map<String, Object> body) {
        Payment p = new Payment();
        p.id = Rand.id("pay");
        p.amount = Req.integer(body, "amount", Rand.amount());
        p.currency = Req.str(body, "currency", Rand.currency());
        p.captureMethod = Req.str(body, "capture_method", "automatic");

        boolean manual = "manual".equalsIgnoreCase(p.captureMethod);
        if (manual) {
            p.status = "requires_capture";
            p.amountCapturable = p.amount;
            p.amountReceived = 0;
        } else {
            p.status = "succeeded";
            p.amountCapturable = 0;
            p.amountReceived = p.amount;
        }

        p.customerId = Req.str(body, "customer_id");
        p.paymentMethodId = Req.str(body, "payment_method", Rand.id("pm"));
        p.paymentMethodDetails = paymentMethodDetails();
        p.description = Req.str(body, "description");
        p.statementDescriptor = Req.str(body, "statement_descriptor");
        p.receiptEmail = Req.str(body, "receipt_email");
        p.receiptUrl = "https://pay.example.com/receipts/" + Rand.id("rcpt").substring(4);
        p.balanceTransaction = Rand.id("txn");
        p.applicationFeeAmount = Req.integer(body, "application_fee_amount");
        p.riskScore = Rand.intRange(0, 100);
        p.refunds = emptyRefundList();
        Map<String, String> meta = Req.metadata(body);
        p.metadata = meta != null ? meta : Rand.metadata();
        p.createdAt = Rand.recentInstant();
        p.updatedAt = Instant.now();
        return p;
    }

    private PaymentMethodDetails paymentMethodDetails() {
        PaymentMethodDetails d = new PaymentMethodDetails();
        d.type = "card";
        d.card = card();
        return d;
    }

    public PaymentList paymentList(int limit, Map<String, Object> filters) {
        PaymentList list = new PaymentList();
        list.data = new ArrayList<>();
        int n = Rand.intRange(1, Math.max(1, Math.min(limit, 10)));
        for (int i = 0; i < n; i++) {
            list.data.add(payment(filters));
        }
        list.meta = listMeta(n);
        return list;
    }

    /* ------------------------------------------------------------------ */
    /* Refunds                                                             */
    /* ------------------------------------------------------------------ */

    public Refund refund(String paymentId, Map<String, Object> body) {
        Refund r = new Refund();
        r.id = Rand.id("re");
        r.amount = Req.integer(body, "amount", Rand.amount());
        r.currency = Rand.currency();
        r.paymentId = paymentId != null ? paymentId : Rand.id("pay");
        r.status = Rand.pick("succeeded", "pending", "processing");
        r.reason = Req.str(body, "reason", Rand.pick("requested_by_customer", "duplicate", "fraudulent"));
        r.balanceTransaction = Rand.id("txn");
        r.receiptNumber = Rand.digits(4) + "-" + Rand.digits(4);
        Map<String, String> meta = Req.metadata(body);
        r.metadata = meta != null ? meta : Rand.metadata();
        r.createdAt = Rand.recentInstant();
        return r;
    }

    private RefundList emptyRefundList() {
        RefundList list = new RefundList();
        list.data = new ArrayList<>();
        ListMeta m = new ListMeta();
        m.hasMore = false;
        m.totalCount = 0;
        m.url = "/refunds";
        list.meta = m;
        return list;
    }

    public RefundList refundList(String paymentId, int limit) {
        RefundList list = new RefundList();
        list.data = new ArrayList<>();
        int n = Rand.intRange(0, Math.max(1, Math.min(limit, 5)));
        for (int i = 0; i < n; i++) {
            list.data.add(refund(paymentId, null));
        }
        list.meta = listMeta(n);
        return list;
    }

    /* ------------------------------------------------------------------ */
    /* Customers                                                           */
    /* ------------------------------------------------------------------ */

    public Customer customer(Map<String, Object> body) {
        Customer c = new Customer();
        c.id = Rand.id("cus");
        c.email = Req.str(body, "email", Rand.email());
        c.name = Req.str(body, "name", Rand.fullName());
        c.phone = Req.str(body, "phone", Rand.phone());
        c.description = Req.str(body, "description");
        c.address = address();
        c.defaultPaymentMethodId = Rand.id("pm");
        c.balance = Rand.intRange(0, 50_000);
        c.currency = Rand.currency();
        c.delinquent = Rand.chance(0.1);
        c.taxExempt = Req.str(body, "tax_exempt", "none");
        Map<String, String> meta = Req.metadata(body);
        c.metadata = meta != null ? meta : Rand.metadata();
        c.createdAt = Rand.recentInstant();
        return c;
    }

    public CustomerList customerList(int limit, Map<String, Object> filters) {
        CustomerList list = new CustomerList();
        list.data = new ArrayList<>();
        int n = Rand.intRange(1, Math.max(1, Math.min(limit, 10)));
        for (int i = 0; i < n; i++) {
            list.data.add(customer(filters));
        }
        list.meta = listMeta(n);
        return list;
    }

    /* ------------------------------------------------------------------ */
    /* Payment methods                                                     */
    /* ------------------------------------------------------------------ */

    public PaymentMethod paymentMethod(String type, String customerId) {
        PaymentMethod pm = new PaymentMethod();
        pm.id = Rand.id("pm");
        pm.type = type != null ? type : "card";
        if ("bank_account".equals(pm.type) || "ach_debit".equals(pm.type) || "sepa_debit".equals(pm.type)) {
            pm.bankAccount = bankAccount();
        } else {
            pm.card = card();
        }
        pm.billingDetails = billingDetails();
        pm.customerId = customerId;
        pm.fingerprint = Rand.id("fp").substring(0, 20);
        pm.isDefault = Rand.bool();
        pm.createdAt = Rand.recentInstant();
        return pm;
    }

    public PaymentMethodList paymentMethodList(String customerId, String type, int count) {
        PaymentMethodList list = new PaymentMethodList();
        list.data = new ArrayList<>();
        int n = Rand.intRange(1, Math.max(1, count));
        for (int i = 0; i < n; i++) {
            list.data.add(paymentMethod(type, customerId));
        }
        list.meta = listMeta(n);
        return list;
    }

    public PaymentMethodValidationResult validation() {
        PaymentMethodValidationResult v = new PaymentMethodValidationResult();
        v.valid = Rand.chance(0.85);
        v.errors = v.valid ? new ArrayList<>()
                : new ArrayList<>(List.of(Rand.pick("card_expired", "invalid_cvc", "card_declined")));
        return v;
    }

    /* ------------------------------------------------------------------ */
    /* Invoices                                                            */
    /* ------------------------------------------------------------------ */

    public Invoice invoice(Map<String, Object> body, String status) {
        Invoice inv = new Invoice();
        inv.id = Rand.id("in");
        inv.status = status;
        inv.customerId = Req.str(body, "customer_id", Rand.id("cus"));
        inv.subscriptionId = Req.str(body, "subscription_id");
        inv.number = "INV-" + Rand.digits(6);
        inv.currency = Rand.currency();

        int subtotal = Rand.amount();
        int tax = (int) Math.round(subtotal * 0.1);
        inv.subtotal = subtotal;
        inv.tax = tax;
        inv.total = subtotal + tax;

        boolean paid = "paid".equals(status);
        inv.amountDue = inv.total;
        inv.amountPaid = paid ? inv.total : 0;
        inv.amountRemaining = paid ? 0 : inv.total;
        if (paid) {
            inv.paidAt = Instant.now();
        }
        if ("void".equals(status)) {
            inv.voidedAt = Instant.now();
        }

        inv.dueDate = LocalDate.now().plusDays(Rand.intRange(7, 30));
        inv.hostedInvoiceUrl = "https://pay.example.com/invoices/" + inv.id.substring(3);
        inv.invoicePdf = inv.hostedInvoiceUrl + "/pdf";
        inv.paymentIntentId = Rand.id("pay");
        inv.collectionMethod = Req.str(body, "collection_method", "charge_automatically");
        inv.footer = Req.str(body, "footer");

        Invoice.Lines lines = new Invoice.Lines();
        lines.data = List.of(invoiceItem(inv.customerId, inv.id, inv.currency, subtotal));
        inv.lines = lines;

        Map<String, String> meta = Req.metadata(body);
        inv.metadata = meta != null ? meta : Rand.metadata();
        inv.createdAt = Rand.recentInstant();
        return inv;
    }

    private InvoiceItem invoiceItem(String customerId, String invoiceId, String currency, int amount) {
        InvoiceItem item = new InvoiceItem();
        item.id = Rand.id("ii");
        item.customerId = customerId;
        item.invoiceId = invoiceId;
        item.amount = amount;
        item.currency = currency;
        item.description = Rand.pick("Subscription", "One-time charge", "Usage fees", "Setup fee");
        item.quantity = 1;
        item.unitAmount = amount;
        item.discountable = true;
        item.createdAt = Rand.recentInstant();
        return item;
    }

    public InvoiceList invoiceList(int limit, Map<String, Object> filters) {
        InvoiceList list = new InvoiceList();
        list.data = new ArrayList<>();
        int n = Rand.intRange(1, Math.max(1, Math.min(limit, 10)));
        for (int i = 0; i < n; i++) {
            list.data.add(invoice(filters, Rand.pick("draft", "open", "paid", "void")));
        }
        list.meta = listMeta(n);
        return list;
    }

    /* ------------------------------------------------------------------ */
    /* Balance                                                             */
    /* ------------------------------------------------------------------ */

    public Balance balance() {
        Balance b = new Balance();
        b.available = List.of(fund(), fund());
        b.pending = List.of(fund());
        b.reserved = List.of(fund());
        return b;
    }

    private BalanceFund fund() {
        BalanceFund f = new BalanceFund();
        f.amount = Rand.intRange(0, 10_000_000);
        f.currency = Rand.currency();
        f.sourceTypes = Map.of("card", f.amount);
        return f;
    }

    public BalanceTransaction balanceTransaction(String id, Map<String, Object> filters) {
        BalanceTransaction t = new BalanceTransaction();
        t.id = id != null ? id : Rand.id("txn");
        t.amount = Rand.amount();
        t.fee = (int) Math.round(t.amount * 0.029) + 30;
        t.net = t.amount - t.fee;
        t.currency = Req.str(filters, "currency", Rand.currency());
        t.type = Req.str(filters, "type", Rand.pick("charge", "refund", "payout", "adjustment", "transfer"));
        t.status = Rand.pick("available", "pending");
        t.source = Rand.id("pay");
        t.description = Rand.pick("Payment", "Refund", "Payout", "Adjustment");
        t.reportingCategory = t.type;
        t.createdAt = Rand.recentInstant();
        t.availableOn = Rand.futureInstant();
        return t;
    }

    public BalanceTransactionList balanceTransactionList(int limit, Map<String, Object> filters) {
        BalanceTransactionList list = new BalanceTransactionList();
        list.data = new ArrayList<>();
        int n = Rand.intRange(1, Math.max(1, Math.min(limit, 10)));
        for (int i = 0; i < n; i++) {
            list.data.add(balanceTransaction(null, filters));
        }
        list.meta = listMeta(n);
        return list;
    }
}
