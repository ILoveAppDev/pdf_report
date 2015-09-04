# pdf_report
A easy PDF report producer

Using PDF to display and store information is widely adopted in almost every aspect of digital age. Unfortunately it is not that easy to produce a complete PDF file without extensive knowledge of programming and tools.

This project is intended to offer a simple and intuitive solution for end user to rely on simple setup at web server, and use a dedicated URL to access the server to get the PDF he/she requires.

My first step for this project is to create a module of Java servlet. The servlet will read its configuration to get data resource information and then use iText library tool to produce PDF.

In this release, user can register JDBC connection information, a sql statement which is responsible to get data from database in a xml-based configuration file, save the file on web server folder. After this setup is done, the servlet, when is initiated, will produce a table view in a PDF file with all column names and row data, and directly output to web user. 
