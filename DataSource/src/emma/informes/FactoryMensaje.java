package emma.informes;

import java.util.ArrayList;
import java.util.List;

public class FactoryMensaje {

    public static List<MensajeInforme> createListMensaje(){

        List<MensajeInforme> mensajeList = new ArrayList<>();
        String html = " <HTML>\n" +
                "\n" +
                "<HEAD>\n" +
                "\n" +
                "<TITLE>Un Titulo para el Browser de turno </TITLE>\n" +
                "\n" +
                "</HEAD>\n" +
                "\n" +
                "<BODY>\n" +
                "\n" +
                "<!-- Aqui va todo lo chachi -->\n" +
                "\n" +
                "<H1>Otro t&iacute;tulo, esta vez m&aacute;s largo. </H1>\n" +
                "\n" +
                "<P> <IMG SRC= \"./felix.gif \"ALIGN= \"MIDDLE \" ALT= \"EL Gato Felix \">Hoola.\n" +
                "\n" +
                "<P>Esto es un parrafo con informacion\n" +
                "\n" +
                "super importante. Notese que las lineas salen pegadas aun dejando\n" +
                "\n" +
                "espacios, saltos de linea, etc. <BR> &#161 Si pongo esto\n" +
                "\n" +
                "si <STRONG>cambia </STRONG> de linea!\n" +
                "\n" +
                "<P>Otro parrafo, esto ya es un poco rollo.\n" +
                "\n" +
                "<H3>Pongamos un subtítulo<H3>\n" +
                "\n" +
                "<P>Por cierto, &#191 que paso con las <A HREF= \"#pepe \">anclas</A>?\n" +
                "\n" +
                "<HR>\n" +
                "\n" +
                "<UL>\n" +
                "\n" +
                "<LI> Esto es una lista no ordenada.\n" +
                "\n" +
                "<LI> Las listas quedan mejor si tienen varios elementos.\n" +
                "\n" +
                "</UL>\n" +
                "\n" +
                "Me voy al <A HREF= \"http://www.iac.es/home.html \">IAC</A>.\n" +
                "\n" +
                "<P>Vamos a crear un <EM>ancla </EM>, o lo que es lo mismo, un <A NAME=\"pepe\">anchor.</A>\n" +
                "\n" +
                ".....................................................\n" +
                "\n" +
                ".....................................................\n" +
                "\n" +
                "</BODY>\n" +
                "\n" +
                "</HTML> ";
        MensajeInforme mensajeInforme = new MensajeInforme("from","to","subject","date");
        mensajeInforme.setContent(html);
        mensajeInforme.setFolder("Folder");
        mensajeList.add(mensajeInforme);
        return mensajeList;

    }
}
