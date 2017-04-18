# nTA
A minimalistic, easy to use Rss reader application.

## Program Scan Result

:white_check_mark: [Metadefender Scanner](https://www.metadefender.com/#!/results/file/YTE3MDQxOFNrYW1tU3E3MGxTSkM3N3I5UUNn/regular/analysis)

# Download Apk
[![Price](https://img.shields.io/chrome-web-store/price/nimelepbpejjlbmoobocpfnjhihnpked.svg?style=plastic)]()
[![TeamCity CodeBetter](https://img.shields.io/badge/size-3.67%20MB-brightgreen.svg)]()
[![Travis branch](https://img.shields.io/badge/platform-android-brightgreen.svg)]()
[![Requirement](https://img.shields.io/badge/android-%3E%3D4.1-orange.svg)]()
[![Travis branch](https://img.shields.io/travis/rust-lang/rust/master.svg)](https://www.dropbox.com/s/xsza3ex4pif49dm/nta-v0-1.apk?dl=0)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]()
***
For download apk without android store please goto ``app/`` folder and Download ``nta-v*-*.apk``

OR 

Click [![Travis branch](https://img.shields.io/travis/rust-lang/rust/master.svg)](https://www.dropbox.com/s/mi5lh5kzuvrng9g/nta-v0-1.apk?dl=0)

# What is nTA ?
nTA is an android app which enable the users to manage their Rss feeds. User can add new sources, manage them and view the article associated with the feeds.

# Permissions:
* Internet
* Access Network State
* Read External Storage
* Write External Storage
These permissions are used to grab the data from the web and to know if user’s internet is available or not so that he can be notified regarding that matter. The Read External Storage permissions is used to retrieve the opml files from either SD card or internal storage. Also, this app will never snoop on your personal information.

# Application features:
* Load Rss feeds quickly
* Add Rss Sources
* Manage Rss Sources
* Archive feeds
* Customizable settings
* Ad free
* export opml and import opml

#How does nTA work ?
nTA uses jsoup xml parser to parse rss feeds and also load the web articles associated with those feeds. It also uses glide to lazy load article images(if available).

# Screenshots
![](https://www.dropbox.com/s/pasf1cdf53spi63/main.png?dl=1)
![](https://www.dropbox.com/s/i4h3p1pu6wboqzn/add.png?dl=1)
![](https://www.dropbox.com/s/7fxan8xm423vrog/add_category.png?dl=1)
![](https://www.dropbox.com/s/nmsxv64950drswn/resource_managment.png?dl=1)
![](https://www.dropbox.com/s/83ud2hvjv54lk7u/settings.png?dl=1)
![](https://www.dropbox.com/s/9cjdbkn0gxcpqvk/about.png?dl=1)

# My Resource not working (منابع من کار نمیکند)

:large_blue_circle: باشد opml توجه داشته باشید فایل به پسوند <br>
:large_blue_circle: file format should be this template *.opml


# For example working

:recycle: this step
