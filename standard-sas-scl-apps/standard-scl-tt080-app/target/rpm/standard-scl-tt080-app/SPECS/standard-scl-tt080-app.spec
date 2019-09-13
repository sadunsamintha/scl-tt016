%define __jar_repack 0
Name: standard-scl-tt080-app
Version: 2.5.15
Release: SNAPSHOT20190913193454
Summary: standard-scl-tt080-app
License: (c) 2009 SICPA Security Solutions S.A.
Vendor: SICPA Security Solutions S.A.
URL: http://www.sicpa.com
Group: sicpa/app/sasscl
Packager: SICPA Security Solutions S.A.
Requires: java
autoprov: yes
autoreq: yes
BuildArch: noarch
BuildRoot: /cygdrive/C/Desenvolvimento/Core_Client/sas-scl-std/standard-sas-scl-apps/standard-scl-tt080-app/target/rpm/standard-scl-tt080-app/buildroot

%description
Standard Projects POM

%install

if [ -d $RPM_BUILD_ROOT ];
then
  mv /cygdrive/C/Desenvolvimento/Core_Client/sas-scl-std/standard-sas-scl-apps/standard-scl-tt080-app/target/rpm/standard-scl-tt080-app/tmp-buildroot/* $RPM_BUILD_ROOT
else
  mv /cygdrive/C/Desenvolvimento/Core_Client/sas-scl-std/standard-sas-scl-apps/standard-scl-tt080-app/target/rpm/standard-scl-tt080-app/tmp-buildroot $RPM_BUILD_ROOT
fi
chmod -R +w $RPM_BUILD_ROOT

%files

 "/usr/scl/app/update"
  "/usr/scl/app//client-installer-1.0.5.jar"

%post
cd /usr/scl/app/
								java -cp client-installer-1.0.5.jar	com.sicpa.standard.tools.installer.InstallerMain -class=com.sicpa.standard.sasscl.installer.SclAppInstaller
								rm -r /usr/scl/app/update
								rm client-installer-1.0.5.jar
								setfacl -R -m user:sicpa_op:rwx /usr/scl/app/
