# Prescribe.ME - Voice Prescription using NLU

![PM-Header-01](https://user-images.githubusercontent.com/96186273/151930633-2cfde444-eebe-4c7f-8cbe-6cb962e69b00.jpg)
An Android Application for doctors to verbally prescribe Medicines

Using this application, doctors can utter Prescriptions in **English** sentences, which will then by converted to a tabular prescription

## Features

### User Profile

User Registration & Authentication is done using [Firebase Authentication](https://firebase.google.com/docs/auth)

The app stores user Profile in [Firebase Realtime Database](https://firebase.google.com/docs/database)

Information Stored include:

* First Name
* Last Name
* Aadhar No
* Contact No
* Clinic/Hospital Visits

The App also stores User Signature in [Firebase Storage](https://firebase.google.com/docs/storage)

### Prescription

Before proceeding for prescription, the app initially asks for patient information.

The app asks for 4 fields related to the patient

* First Name
* Last Name
* Age
* Gender

After Patient Info is entered, the next step is for Prescription

Here, the user is prompted for 3 fields: <br>

* Diagnosis (optional)
* Prescription
* Additional Information (optional)

#### **Prescribing Medicines**

The user can utter (by voice) or type in the prescription (1 sentence per drug) in simple **English** sentences. Then, they can click on the _Prescribe Above Sentence_ button once sentence is finalised. Now, this sentence is sent through API to our ML Model hosted on [Heroku App](https://devcenter.heroku.com/). The Model then predicts intent & slots. These slots are then returned to the app where they are displayed in tabular format

Example:
Prescription: _I suggest taking Tablet Dolo 650 Paracetamol for 15 days 3 times a week after breakfast_
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
