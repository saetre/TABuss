Steps for compiling the Android app in your debug environment:
--------------------------------------------------------------
Follow the instructions here to make your own debug key:
http://code.google.com/android/add-ons/google-apis/mapkey.html

Alternative 1 (recommended)
- All users use the same debug.keystore
- Copy BusTUCApplication/debug.keystore to $HOME/.android
- Check android:apiKey in the file BusTUCApplication/res/layout/main.xml
  It should be "05MLhk0p9hp3JBgHGNN8QZVa-D8D3Pz6h4TuRjA"

Alternative 2 (the old way - every developer has to do it every time code is fetched from another PC):
- Replace the key in following line from BusTUCApplication/res/layout/main.xml
- android:apiKey="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
  with the key for the machine you compile on

