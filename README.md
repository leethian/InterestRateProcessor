# InterestRateProcessor
(A) Design patterns
No specific design patterns.

(B) Build (Maven Build):
Use IntelliJ IDE (or other IDE which supports Maven) to open Project folder InterestRateProcessor which would compile the program.

(C) Running the program
1. Use IntelliJ IDE (or other IDE which supports Maven) to Run main class src/app/CompareBankToFCInterestRates with below parameters:

a. proxy - To access the MAS API, proxy is used. Provide the proxy to be used.

b. proxy port - Provide port for proxy

c. dates for data (format: mmm-yyyy; delimiter: ,). E.g. Jan-2017,Feb-2017,Mar-2017,Apr-2017,May-2017,Jun-2017,Jul-2017

Example:
1. With proxy: [proxy] [proxy port] Jan-2017,Feb-2017,Mar-2017,Apr-2017,May-2017,Jun-2017,Jul-2017
2. Without proxy: null 0 Jan-2017,Feb-2017,Mar-2017,Apr-2017,May-2017,Jun-2017,Jul-2017

(D) Assumptions
1. For fixed deposit rates, 12 month interest rate is used
2. Dates data is limited to 60 months data
3. Dates data input in parameter is to be ascending order (i.e. Jan-2017,Feb-2017,Mar-2017, etc)