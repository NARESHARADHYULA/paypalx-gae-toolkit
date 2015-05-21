The PayPal X Payments Platform provides several Adaptive Payments APIs that enable you to provision payments through applications or services running on the Cloud. Using the PayPal X Toolkit for Google App Engine, applications built on Google App Engine can quickly handle payments, preapprovals for payments, refunds, and even use the Foreign Exchange currency conversion rates. Please check https://www.X.com for more information about PayPal's Adaptive Payments APIs.

Just to give you an idea - here is a high level systems diagram showing where the toolkit fits in your application on Google App Engine.



&lt;center&gt;


<img src='http://lh5.ggpht.com/_jvhSt29ZTys/S_SEbrFmg6I/AAAAAAAAADQ/OYgyrPp178M/s720/paypalx-gae-toolkit.png' />


&lt;/center&gt;



This project includes a library (jar file) that can be used by any application built and running on Google App Engine  to integrate with PayPal X Payments Platform, and make use of various API operations provided.

PayPal X Payments Platform Toolkit for Google App Engine currently includes support for [Adaptive Payments](https://www.x.com/community/ppx/adaptive_payments) APIs only. Support for Adaptive Accounts APIs will be added soon along with IPN Listener functionality.

Please feel free to explore more in the source code and please let me know if you would like to help me in fixing any bugs found and/or improve the toolkit in general. In the source code you will notice, I am experimenting with providing a functional layer of classes that provide more specific functionality (ChainedPay, ParallelPay, RefundPartialPayment, PreapprovedParallelPay, CreatePreapprovalForPeriodicPayments, etc..). The idea behind them is to help you quickly get started and explore the APIs while writing the code and handling the exceptions about missing required parameters than spending a whole lot of time in reading the documentation. So please check out the javadocs linked from the side panel and let me know your feedback about it - it will help me make this toolkit more useful, easy to use and get started with.

The sample directory contains 3 eclipse sample apps: <br>
- an API Scratch Pad that let's you explore and play with the Adaptive Payments APis, <br>
- a functional API Scratch Pad that let's you explore the new higher level classes that provide more specific functionality than the generic API requests, and  <br>
- a sample Photo Printing App (PicMart) that grabs the pictures from Picasa and let's users buy prints using PayPal X toolkit.<br>
<br>
All 3 sample apps come with test API credentials hardcoded in the web.xml files. Please update them with your Sandbox API credentials so you will have more control on the sandbox environment.<br>
<br>
Cheers, <br>
Praveen <br>

<b>PayPal X Developer Network</b>

<a href='https://www.x.com'>https://www.x.com</a>