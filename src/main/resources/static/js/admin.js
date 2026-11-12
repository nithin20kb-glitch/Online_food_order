/* ==============================================================================
   FoodExpress - Admin Dashboard JavaScript
   ============================================================================== */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Auto submit availability toggle form on change
    const toggleInputs = document.querySelectorAll('.stock-toggle-checkbox');
    toggleInputs.forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            const form = this.closest('form');
            if (form) {
                form.submit();
            }
        });
    });

    // 2. Order status modal / reason prompt for rejection
    const statusSelects = document.querySelectorAll('.admin-order-status-select');
    statusSelects.forEach(select => {
        select.addEventListener('change', function () {
            const form = this.closest('form');
            if (this.value === 'REJECTED_REFUNDED') {
                const reason = prompt('Please enter the reason for rejection (this will be sent to the customer with full refund):', 'Kitchen is currently overwhelmed');
                if (reason !== null && reason.trim() !== '') {
                    const reasonInput = form.querySelector('input[name="reason"]');
                    if (reasonInput) reasonInput.value = reason;
                    form.submit();
                } else {
                    // Reset selection
                    window.location.reload();
                }
            } else {
                form.submit();
            }
        });
    });

    // 3. Quick Table Search Filter
    const tableSearch = document.getElementById('tableSearchInput');
    if (tableSearch) {
        tableSearch.addEventListener('keyup', function () {
            const term = this.value.toLowerCase();
            const rows = document.querySelectorAll('.searchable-row');
            rows.forEach(row => {
                const text = row.innerText.toLowerCase();
                row.style.display = text.includes(term) ? '' : 'none';
            });
        });
    }
});
