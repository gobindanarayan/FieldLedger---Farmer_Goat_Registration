document.addEventListener('DOMContentLoaded', () => {
    const localToday = new Date();
    localToday.setHours(0, 0, 0, 0);
    const todayText = new Date(localToday.getTime() - localToday.getTimezoneOffset() * 60000)
        .toISOString().slice(0, 10);
    document.querySelectorAll('input[data-not-future]').forEach((input) => {
        input.max = todayText;
    });

    document.querySelectorAll('.flash-close').forEach((button) => {
        button.addEventListener('click', () => button.closest('.flash')?.remove());
    });

    const vaccinationForm = document.querySelector('[data-date-check]');
    if (vaccinationForm) {
        const givenDate = vaccinationForm.querySelector('#vaccinationDate');
        const dueDate = vaccinationForm.querySelector('#nextDueDate');
        const checkDates = () => {
            if (!givenDate || !dueDate) return;
            dueDate.setCustomValidity(dueDate.value && givenDate.value && dueDate.value <= givenDate.value
                ? 'The next due date must be after the vaccination date.' : '');
        };
        givenDate?.addEventListener('change', checkDates);
        dueDate?.addEventListener('change', checkDates);
        vaccinationForm.addEventListener('submit', checkDates);
    }
});
