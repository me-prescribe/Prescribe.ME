# Prescribe.ME - Voice Prescription using NLU

![PM-Header-01](https://user-images.githubusercontent.com/96186273/151930633-2cfde444-eebe-4c7f-8cbe-6cb962e69b00.jpg)

An Android Application for doctors to **verbally** prescribe Medicines

Using this application, doctors can utter Prescriptions in **English** sentences, which will then by converted to a tabular prescription

Each & Every Field mentioned or not mentioned below excluding password can be entered orally by the user with the help of [Google Speech to Text](https://cloud.google.com/speech-to-text).

## Features

### User Profile

User Registration & Authentication is done using [Firebase Authentication](https://firebase.google.com/docs/auth).

The app stores user Profile in [Firebase Realtime Database](https://firebase.google.com/docs/database).

Information Stored include:

* First Name
* Last Name
* Aadhar No
* Contact No
* Clinic/Hospital Visits
* Registration No
* Qualifications

The App also stores User Signature in [Firebase Storage](https://firebase.google.com/docs/storage).

### Prescription

Before proceeding for prescription, the app initially asks for patient information.

The app asks for 4 fields related to the patient:

* First Name
* Last Name
* Age
* Gender

After Patient Info is entered, the next step is for Prescription.
Here, the user is prompted for 3 fields:

* Diagnosis (optional)
* Prescription
* Additional Information (optional)

#### **Prescribing Medicines**

The user can utter (by voice) or type in the prescription (1 sentence per drug) in simple **English** sentences. Then, they can click on the _Prescribe Above Sentence_ button once sentence is finalised. Now, this sentence is sent through API to our ML Model hosted on [Heroku App](https://devcenter.heroku.com/). The Model then predicts intent & slots. These slots are then returned to the app where they are displayed in tabular format.

Example:

> Prescription: _"I suggest taking Tablet Dolo 650 Paracetamol for 15 days 3 times a week after breakfast"_

|Slot Name|Slot Value|
|---------|----------|
|Drug|Tablet Dolo 650|
|INN|Paracetamol|
|Frequency|3 times a week|
|Consumption|after breakfast|
|Duration|15 days|
|Gap|-|
|Quantity|-|
|Route|-|
|Condition|-|
|Fasting|-|

The user may prescribe as many times as they wish. Once, they have prescribed all drugs necessary, they can proceed to view the prescription.

### Displaying Prescription

The user is displayed:

* Doctor Profile
  * Name
  * Qualifications
  * Clinic/Hospital
  * Contact No
* Patient Profile
  * Name
  * Age
  * Gender

for confirmation.

#### **HTML**

After the user has confirmed above mentioned information, a prescription is generated. The Prescription contains the user's info next to a [Caduceus](https://en.wikipedia.org/wiki/Caduceus) which is a symbol of the doctor. Followed by the Patient's Info, Date, and a unique Prescription ID.

This is then followed by the Prescription itself, which is the above table rotated. All prescriptive sentences are included in this table. i.e. non-prescriptive sentences (normal conversatory sentences) are not included.

After this, is the Signature & Name of the Doctor.

![image](https://user-images.githubusercontent.com/96186273/151956042-bb13f903-c3fb-4b66-96de-7de5c5fd783c.png)


#### **PDF**

The above generated HTML Prescription is converted to a PDF. Using [HTMLtoPDFConvertor](https://github.com/mddanishansari/html-to-pdf-convertor), the PDF is displayed in the app. The PDF is opened in print viewed, which the user can then save on his phone or print using the print button.

### Dark Mode

We are proud to present the app in Dark Mode. Instead of having forced dark modes or too much brightness when unnecessary, users can switch to Dark Mode. Implemented using separate `theme.xml` , `color.xml` and `style.xml` files in the [`values`](https://github.com/me-prescribe/Prescribe.ME/tree/master/app/src/main/res/values) & [`values-night`](https://github.com/me-prescribe/Prescribe.ME/tree/master/app/src/main/res/values-night) repositories.

## Add-ons used

* Firebase Authentication used for Authentication of Users
* Firebase Realtime Database used for storing User Profile
* Firebase Storage used for storing User Signature
* Heroku App used for ML Model
* PSPDFKit used for PDF Generation
* Firebase App Tester for sending out versions for testing
* Firebase Crashlytics for logging errors
* Firebase Performance Monitoring for logging performance
* Google Analytics for tracking usage
* Google Speech to Text for converting speech to text

## [Privacy Policy](https://github.com/me-prescribe/Prescribe.ME/PRIVACY.MD)

Privacy Policy has been published separately. Encompassing the Privacy Policy and Terms of Service related to User Profile, Prescription, and Signature storage & display.

## Other Important Information

* Colours Used:
  * Light Mode:
    * Background: White (0xFFFFFF)
    * Text: Navy Blue (0x000080)
    * Error Text: Red (0xFF0000)
  * Dark Mode:
    * Background: Black (0x000000)
    * Text: Gold (0xD4AF37)
    * Error Text: Red (0xFF0000)
* Fonts Used:
  * [Righteous](https://fonts.google.com/specimen/Righteous) for Branding
  * [Berkshire Swash](https://fonts.google.com/specimen/Berkshire+Swash) for Doctor Name in Prescription
  * [EB Garamond](https://fonts.google.com/specimen/EB+Garamond) for other Doctor Info in Prescription
  * [Allerta Stencil](https://fonts.google.com/specimen/Allerta+Stencil) for Patient Info in Prescription
  * [Cantata One](https://fonts.google.com/specimen/Cantata+One) for Table Heading in Prescription
  * [Aldrich](https://fonts.google.com/specimen/Aldrich) for Drug Name & INN
  * [Baumans](https://fonts.google.com/specimen/Baumans) for Other Table Contents
  * [Cabin](https://fonts.google.com/specimen/Cabin) for Diagnosis & Additional Information
