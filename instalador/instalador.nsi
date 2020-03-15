# Nombre del instalador
Name "Instalador ClienteCorreo"

# El nombre del instalador
OutFile "ClienteCorreo.exe"

# Configuramos la ruta por defecto donde se instala
InstallDir $DESKTOP\ClienteCorreoEmma

# Pedimos permisos para Windows
RequestExecutionLevel admin

# Pantallas que hay que mostrar del instalador
Page directory
Page instfiles

# Cambiar el idioma
!include "MUI.nsh"
!insertmacro MUI_LANGUAGE "Spanish"

# Seccion principal
Section

messageBox MB_OK "Gracias"

# Establecemos la ruta de instalacion al directorio de instalacion
SetOutPath $INSTDIR

# Anadimos a nuestro paquete instalador los siguientes archivos
File /r "..\out\artifacts\ClienteCorreo_jar\*"
SetOutPath $INSTDIR\help
File  "..\help\articles.zip"

# Anadimos nuestro JavaFX y JRE.
SetOutPath $INSTDIR\javafx
File /r "C:\Users\Emma\Desktop\Emma\Javajdk\javafx-sdk-13\*"
SetOutPath $INSTDIR\java-runtime
File /r "C:\Users\Emma\Desktop\Emma\Javajdk\jdk-13\*"

SetOutPath $INSTDIR
CreateShortCut \
  `$DESKTOP\ClienteCorreo.lnk` \ 
  `$INSTDIR\java-runtime\bin\java.exe` \
  `--module-path "$INSTDIR\javafx\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web,javafx.base --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED -jar "$INSTDIR\ClienteCorreo.jar"` \


# Creamos el desinstalador
writeUninstaller "$INSTDIR\uninstall.exe"

# Creamos un acceso directo apuntando al desinstalador
createShortCut "$DESKTOP\Desinstalar.lnk" "$INSTDIR\uninstall.exe"

# Fin de la seccion

SectionEnd

# Seccion del desintalador
Section "uninstall"

delete "$INSTDIR\uninstall.exe"

RmDir /r "$INSTDIR"


# Borramos el acceso directo del menu de inicio
delete "$DESKTOP\Desinstalar.lnk"
delete "$DESKTOP\ClienteCorreo.lnk"

# Fin de la seccion del desinstalador
SectionEnd
