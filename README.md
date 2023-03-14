# Official NZSL Dictionary App for Android devices

This repository holds the code necessary to develop the NZSL Dictionary App for Android. This codebase can be imported into Android Studio and you are invited to fork it to add features and bug fixes.

The original code was developed by [Greg Hewgill](http://hewgill.com/) and generously gifted to the [Deaf Studies Research Unit](http://www.victoria.ac.nz/lals/centres-and-institutes/dsru) of [Victoria University of Wellignton](http://www.victoria.ac.nz/). It is maintained by [Ackama](https://www.ackama.com/).

# Dictionary data

The dictionary is updated monthly, with data released publicly at https://github.com/ODNZSL/nzsl-dictionary-scripts/releases.

While we keep a version of the dictionary database up-to-date in this repository, you
can download the latest version at any time by placing the "nzsl.dat" file in `app/src/main/assets/db/nzsl.dat`.

# Missing images?

This repository does not include images for the database of signs. Just like the dictionary data, we prepare a public export of published sign data monthly, including preprocessed images for use in the native Android and iOS apps.

Find the latest release at https://github.com/ODNZSL/nzsl-dictionary-scripts/release, from which you can download `assets.tar.gz`, and place the extracted 'assets' folder at `app/src/main/assets/images/signs`.

e.g.

```
tar -xf assets.tar.gz
rm -r app/src/main/assets/images/signs
mv assets app/src/main/assets/images/signs
```

# Android and iOS features

We maintain an iOS and Android version of the app and it is our preference to keep features standard between both apps. We would love you to check out the [iOS App code](https://github.com/ODNZSL/nzsl-dictionary-ios) and consider contributing to both via Github Pull Requests.

# Android bug fixes

If you find a bug and would like to report it, please open a Github issue. If you have a fix for the issue please open a Github Pull Request.

# Contributions

Contributions are welcome for this project. Please comment on an issue if you are willing to work on it and an administrator will give you further information if required.
To contribute, make a fork of this repository and branch off the master branch.
Create a pull request against the ODNZSL master branch.
Two approvals are required before a PR can be merged.

# Deployment

To deploy to production: merge a feature branch into ODNZSL master.
A repository administrator will perform the necessary actions when a release is planned.

# License

This code is available under the [MIT license](https://github.com/ODNZSL/nzsl-dictionary-android/blob/master/LICENSE.txt).
