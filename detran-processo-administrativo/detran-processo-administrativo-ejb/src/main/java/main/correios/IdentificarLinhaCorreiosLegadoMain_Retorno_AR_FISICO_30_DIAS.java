package main.correios;


import java.io.File;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class IdentificarLinhaCorreiosLegadoMain_Retorno_AR_FISICO_30_DIAS {
    
//    private static final Logger LOG = Logger.getLogger(IdentificarLinhaCorreiosLegadoMain.class);

    public static void main(String[] args) {
        
        try {
            
            System.out.println("Data inicio:" + new Date().toString());
        
            List<String> pas 
                = FileUtils
                    .readLines(
                        new File("/home/desenvolvimento/DEV/scripts sql/20190605 - COMPLETO Linhas Correios - ITEM 02 - Tarefa #14899 - ARQUIVO FISICO - 30DIAS - PESQUISAR.txt"), 
                        "UTF-8"
                    );

            Integer totalPasEncontrados = 0;

            List<File> listOfFiles = 
                    (List<File>) FileUtils.listFiles(
                            new File("/home/desenvolvimento/DEV/scripts sql/20190604 - 01_FTP_PA_BKP"), 
                            new String[] {"txt", "TXT"}, 
                            true
                    );

            for (int i = 0; i < pas.size(); i++) { 

                String pa = pas.get(i);

                for (File file : listOfFiles) {
                    
                    List<String> listaLinhas 
                        = FileUtils.readLines(file, "IBM850");

                    for (String linha : listaLinhas) {
                        
                        if (linha.contains(pa)) {
                        
                            StringBuilder pasEncontrados = new StringBuilder();
                            
                            pasEncontrados.append(pa.substring(0, 13)).append("|").append(linha).append("|").append(pa).append("|").append(file.getName()).append(System.getProperty("line.separator"));
                            
                            FileUtils
                                .writeStringToFile(
                                    new File("/home/desenvolvimento/DEV/scripts sql/20190605 - COMPLETO Linhas Correios - ITEM 02 - Tarefa #14899 - ARQUIVO FISICO - 30DIAS - PESQUISAR - ENCONTRADOS.txt"),
                                    pasEncontrados.toString(),
                                    "UTF-8",
                                    Boolean.TRUE
                                );
                            
                            totalPasEncontrados++;
                        }
                    }
                }
            }
            
            System.out.println("Total encontrado:" + totalPasEncontrados);
            System.out.println("Total pesquisado:" + pas.size());

            System.out.println("-------------------------------------------");
        
        } catch (Exception e) {
            
            System.out.println("Erro capturado.");
            
            e.printStackTrace();
            
        } finally {
            
            System.out.println("Data fim:" + new Date().toString());
        }
    }
}
