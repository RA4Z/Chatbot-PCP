package automatismos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PythonExecutor {

    public static List<Dados> executarScript(String argumento) throws IOException, InterruptedException {
        String scriptPath = "./scripts/export_text_from_json.exe";
        String[] command = {scriptPath, argumento};
        ProcessBuilder pb = new ProcessBuilder(command);

        Process process = pb.start();

        List<Dados> resposta = new ArrayList<>();

        // Lê a saída padrão do processo Python com encoding UTF-8
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.ISO_8859_1));
        String line;

        while ((line = reader.readLine()) != null) {
            // Remove caracteres especiais e espaços em branco
            line = line.replaceAll("[{}']", "").trim();

            // Divide a linha em pares "chave:valor"
            Dados dados = getDados(line);
            resposta.add(dados);
        }

        // Aguarda o término do processo Python
        process.waitFor();

        return resposta;
    }

    private static Dados getDados(String line) {
        String[] partes = line.split(",");

        Dados dados = new Dados();
        for (String parte : partes) {
            String[] chaveValor = parte.split(": ");
            if (chaveValor.length == 2) {
                String chave = chaveValor[0].trim();
                String valor = chaveValor[1].trim();

                switch (chave) {
                    case "Name":
                        dados.setNome(valor);
                        break;
                    case "Path":
                        dados.setPath(valor);
                        break;
                    case "PathProcedure":
                        dados.setPathProcedure(valor);
                        break;
                    case "LastUpdate":
                        dados.setLastUpdate(valor);
                        break;
                    case "Status":
                        dados.setStatus(valor);
                        break;
                }
            }
        }
        return dados;
    }
}
