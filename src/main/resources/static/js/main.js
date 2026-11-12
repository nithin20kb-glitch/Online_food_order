/* ==============================================================================
   FoodExpress - Main Interactive JavaScript
   ============================================================================== */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity 0.5s ease, max-height 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // 2. Checkout Payment Method Tabs Switcher
    const paymentRadios = document.querySelectorAll('input[name="paymentMethod"]');
    const paymentSections = {
        'CARD': document.getElementById('cardPaymentSection'),
        'UPI': document.getElementById('upiPaymentSection'),
        'NET_BANKING': document.getElementById('netBankingPaymentSection'),
        'COD': document.getElementById('codPaymentSection')
    };

    function updatePaymentView(selectedMethod) {
        Object.keys(paymentSections).forEach(key => {
            const section = paymentSections[key];
            if (section) {
                if (key === selectedMethod) {
                    section.style.display = 'block';
                } else {
                    section.style.display = 'none';
                }
            }
        });
    }

    if (paymentRadios.length > 0) {
        paymentRadios.forEach(radio => {
            radio.addEventListener('change', function () {
                updatePaymentView(this.value);
            });
            if (radio.checked) {
                updatePaymentView(radio.value);
            }
        });
    }

    // 3. Quick Location Change handler in Hero section
    const locationSelector = document.getElementById('heroLocationSelect');
    if (locationSelector) {
        locationSelector.addEventListener('change', function () {
            const selected = this.value;
            const searchForm = document.getElementById('heroSearchForm');
            if (searchForm) {
                searchForm.submit();
            }
        });
    }

    // 4. Quantity inputs validation (ensure not zero or negative)
    const qtyInputs = document.querySelectorAll('.qty-input');
    qtyInputs.forEach(input => {
        input.addEventListener('change', function () {
            if (parseInt(this.value) < 1 || isNaN(this.value)) {
                this.value = 1;
            }
        });
    });
});

// Toast notification helper
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type}`;
    toast.style.position = 'fixed';
    toast.style.bottom = '20px';
    toast.style.right = '20px';
    toast.style.zIndex = '9999';
    toast.style.minWidth = '250px';
    toast.style.boxShadow = '0 10px 25px rgba(0,0,0,0.15)';
    toast.innerHTML = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 400);
    }, 3500);
}
