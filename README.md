## Om tjänsten

Tjänsten SkitEnkelts primära syfte är att tillgodose information om toaletter i Malmö. Användaren har möjlighet att se de offentliga toaletterna på en karta i området och hitta information om en specifik toalett genom att klicka på den på kartan. Tjänsten tillhandahåller även möjlighet att betygsätta toaletterna och söka efter toaletter baserat på avstånd eller betyg.

Tjänsten lämpar sig för de som lider av akut behov av att “gå” eller den som behöver planera sitt toalettbesök på förhand. Information ges om de olika toaletterna, exempelvis huruvida avgift krävs för tillträde eller antal toaletter och antal skötbord.

## Användarmanual

### Installationsinstruktioner

Se till att du har installerat Java 17 eller en senare version i din dator.  
Om du behöver installera Java, gå till https://www.java.com/en/download/manual.jsp och följ instruktionerna  
Vi rekommenderar att köra projektet på IntelliJ eller Visual Studio Code

### Körinstruktioner

Gå till https://github.com/Gustav-HL/SkitEnkelt och klona projektet till din dator  
Öppna projektet på din code editor  
Kör en git pull för att säkerställa att du har den senaste versionen av koden  
Se till att du har installerat Maven plugin  
Gå till src > Backend > App  

#### Om du kör IntelliJ:

Gå till Run > Edit Configurations  
Tryck på “+”-tecken på den övre vänstra sidan  
Välj “Application” från menyn.  
På fältet “Name”, skriv “App”.  
Under rubriken “Build and Run”, där det stör “Main Class”, tryck på symbolen på högersidan. En ny ruta kommer att öppnas.  
I “Search”-fältet, välj din App-klass. Rutan kommer att stängas.  
Tryck på “Apply”. Sedan “OK”.  
Tryck på Play-knappen på den övre högra sidan av din code editor för att starta appen.  

Index.html bör starta i webbläsaren. Ifall den inte gör det klicka på länken i terminalen eller skriv http://localhost:7070/index.html  

Alternativt:  

Installera extension för Java Extension Pack och Maven  
Öppna projektet där pom.xml ligger  
Låt Maven ladda in dependencies  
Tryck Ctrl + Shift + P  
Skriv: Java: Clean Java Language Server Workspace och låt allt ladda om  
Öppna projektet i huvudfoldern  
Kör App.java  

Index.html bör starta i webbläsaren, ifall den inte gör det klicka på länken i terminalen eller skriv http://localhost:7070/index.html  
Testa hemsidan och njut!

### Om du kör Visual Studio Code:

#### Krav

Java 17  
Maven  
VS Code-tillägget Extension Pack for Java (rekommenderat)  
VS Code tillägget Live Server (rekommenderat)

### Öppna projektet:

1. Öppna VS Code  
2. File -> Open Folder  
3. Välj projektmappen SKITENKELT  

### Starta backend:

1. Öppna VS Code-terminal : Terminal -> New Terminal i menyn högst upp  
2. Gå in i backend-mappen: I terminalen skriver du “cd Backend”. Nu ska din terminal stå i Backend.  
3. I terminalen skriver du sen “mvn clean package” - tryck enter.  
4. Om allt går bra ser du texten “build success” eller liknande.  
5. Starta servern genom att skriva “java -jar target/SkitEnkelt-1.0.0-jar-with-dependencies.jar” i terminalen. Tryck enter. Öppna därefter webbläsaren och testa http://localhost:7070/toilets  
6. Hitta en toalett nära dig.  

### Starta frontend:

1. Öppna Frontend/index.html i VS Code  
2. Högerklicka på filen Open with Live Server  
3. Sidan öppnas i webbläsaren och ska nu kunna hämta data från backend

### Vanliga fel:

“Mvn: command not found” - Maven är inte installerat eller ligger inte i PATH  
Lösning: Installera Maven och starta om VS Code och terminalen.  

Live Server funkar inte - Kontrollera att tillägget Live Server är installerat i VS Code. Högerklicka på Frontend/index.html -> Open with Live Server
