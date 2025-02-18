# Maize Disease Detection Application


## Overview

This project aims to develop a mobile application for automated maize disease detection using Convolutional Neural Networks (CNNs). The application is designed to help smallholder farmers in Makueni County, Kenya, detect and classify common maize diseases such as Blight, Common Rust, and Gray Leaf Spot. The system provides real-time disease diagnosis, recommends appropriate fungicides, and connects farmers with agricultural extension officers for further consultation.

## Features

- **Disease Detection**: The application uses a CNN model trained on maize leaf images to detect and classify diseases.
- **Fungicide Recommendations**: Based on the detected disease, the app recommends appropriate fungicides.
- **Farmer-Extension Officer Connection**: Farmers can connect with extension officers for personalized advice and consultation.
- **User-Friendly Interface**: The app is designed to be intuitive and easy to use, even for farmers with limited technological experience.

## Technologies Used

- **Machine Learning**: TensorFlow for training the CNN model.
- **Mobile Development**: Java for Android app development.
- **Backend**: Flask API for handling image uploads and predictions.
- **Database**: Firebase for storing user data and disease information.

## Installation

To run this project locally, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/VictorNzyoka/maize-plant-disease-predictor.git
   cd maize-disease-detection
