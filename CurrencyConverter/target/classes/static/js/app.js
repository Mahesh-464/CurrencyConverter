
       // Currency Converter JavaScript
	class CurrencyConverter {
		constructor() {
		this.form = document.getElementById('currencyForm');
	this.resultSection = document.getElementById('resultSection');
	this.convertBtn = document.getElementById('convertBtn');
	// API URL - works for both development and production
	this.baseURL = window.location.origin + '/api/currency';

	this.initEventListeners();
           }

	/**
	 * Initialize event listeners
	 */
	initEventListeners() {
		this.form.addEventListener('submit', (e) => {
			e.preventDefault();
			this.handleConversion();
		});

	// Add real-time validation
	const inputs = this.form.querySelectorAll('input, select');
               inputs.forEach(input => {
		input.addEventListener('change', () => this.validateForm());
               });
           }

	/**
	 * Validate form inputs
	 */
	validateForm() {
               const sourceCurrency = document.getElementById('sourceCurrency').value;
	const targetCurrency = document.getElementById('targetCurrency').value;
	const amount = document.getElementById('amount').value;

               const isValid = sourceCurrency && targetCurrency && amount && amount > 0;
	this.convertBtn.disabled = !isValid;

	return isValid;
           }

	/**
	 * Handle currency conversion
	 */
	async handleConversion() {
               if (!this.validateForm()) {
		this.showError('Please fill in all fields with valid values');
	return;
               }

	const formData = new FormData(this.form);
	const sourceCurrency = formData.get('sourceCurrency');
	const targetCurrency = formData.get('targetCurrency');
	const amount = parseFloat(formData.get('amount'));

	// Check if converting same currency
	if (sourceCurrency === targetCurrency) {
		this.showResult(amount, sourceCurrency, targetCurrency);
	return;
               }

	this.showLoading();

	try {
                   const convertedAmount = await this.callBackendAPI(sourceCurrency, targetCurrency, amount);
	this.showResult(convertedAmount, sourceCurrency, targetCurrency);
               } catch (error) {
		console.error('Conversion error:', error);
	this.showError('Failed to convert currency. Please try again.');
               }
           }

	/**
	 * Call the Java backend API
	 */
	async callBackendAPI(sourceCurrency, targetCurrency, amount) {
               const url = `${this.baseURL}/convert?` +
	`from=${encodeURIComponent(sourceCurrency)}&` +
	`to=${encodeURIComponent(targetCurrency)}&` +
	`amount=${encodeURIComponent(amount)}`;

	const response = await fetch(url, {
		method: 'GET',
	headers: {
		'Content-Type': 'application/json',
	'Accept': 'application/json'
                   }
               });

	if (!response.ok) {
                   const errorText = await response.text();
	throw new Error(`HTTP ${response.status}: ${errorText}`);
               }

	const data = await response.json();
	return data.convertedAmount;
           }

	/**
	 * Display loading state
	 */
	showLoading() {
		this.resultSection.innerHTML = `
                   <div class="loading">
                       <div class="spinner-border spinner-border-sm me-2" role="status"></div>
                       Converting...
                   </div>
               `;
	this.convertBtn.disabled = true;
           }

	/**
	 * Display conversion result
	 */
	showResult(convertedAmount, fromCurrency, toCurrency) {
               const formattedAmount = this.formatCurrency(convertedAmount, toCurrency);

	this.resultSection.innerHTML = `
	<div class="result-amount fade-in">
		${formattedAmount}
	</div>
	`;

	this.convertBtn.disabled = false;
           }

	/**
	 * Display error message
	 */
	showError(message) {
		this.resultSection.innerHTML = `
                   <div class="error fade-in">
                       <svg width="20" height="20" fill="currentColor" class="me-2" viewBox="0 0 16 16">
                           <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM8 4a.905.905 0 0 0-.9.995l.35 3.507a.552.552 0 0 0 1.1 0l.35-3.507A.905.905 0 0 0 8 4zm.002 6a1 1 0 1 0 0 2 1 1 0 0 0 0-2z"/>
                       </svg>
                       ${message}
                   </div>
               `;
	this.convertBtn.disabled = false;
           }

	/**
	 * Format currency with proper locale and currency symbol
	 */
	formatCurrency(amount, currency) {
               try {
                   return new Intl.NumberFormat('en-US', {
		style: 'currency',
	currency: currency,
	minimumFractionDigits: 2,
	maximumFractionDigits: 6
                   }).format(amount);
               } catch (error) {
                   // Fallback formatting if currency is not supported
                   return `${amount.toFixed(2)} ${currency}`;
               }
           }
       }

       // Initialize the application when DOM is loaded
       document.addEventListener('DOMContentLoaded', () => {
		new CurrencyConverter();
       });

       // Add some interactive effects
       document.addEventListener('DOMContentLoaded', () => {
           // Add hover effects to form elements
           const formElements = document.querySelectorAll('.form-select, .form-control');
           formElements.forEach(element => {
		element.addEventListener('focus', function() {
			this.parentElement.style.transform = 'translateY(-2px)';
		});

	element.addEventListener('blur', function() {
		this.parentElement.style.transform = 'translateY(0)';
               });
           });
       });
