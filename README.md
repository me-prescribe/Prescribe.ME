# Prescribe.ME - Voice Prescription using NLU
![PM-Header-01](https://user-images.githubusercontent.com/96186273/151930633-2cfde444-eebe-4c7f-8cbe-6cb962e69b00.jpg)
An Android Application for doctors to verbally prescribe Medicines <br>
Using this application, doctors can utter Prescriptions in **English** sentences, which will then by converted to a tabular prescription
## Features
### User Profile
User Registration & Authentication is done using [Firebase Authentication](https://firebase.google.com/docs/auth) <br>
The app stores user Profile in [Firebase Realtime Database](https://firebase.google.com/docs/database) <br>
Information Stored include:
<ul><li>First Name<li>Last Name<li>Aadhar No<li>Contact No<li>Clinic/Hospital Visits</ul>
The App also stores User Signature in [Firebase Storage](https://firebase.google.com/docs/storage/) <br>
### Prescription
Before proceeding for prescription, the app initially asks for patient information. <br>
The app asks for 4 fields related to the patient <br>
<ul><li>First Name<li>Last Name<li>Age<li>Gender</ul>
After Patient Info is entered, the next step is for Prescription <br>
Here, the user is prompted for 3 fields: <br>
<ul><li>Diagnosis (optional)<li>Prescription<li>Additional Information (optional)</ul>
## Prescribing Medicines
The user can utter (by voice) or type in the prescription (1 sentence per drug) in simple **English** sentences. Then, they can click on the _Prescribe Above Sentence_
