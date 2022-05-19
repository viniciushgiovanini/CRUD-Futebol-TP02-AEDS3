import java.io.RandomAccessFile;

public class listainvertida {

  private String nomeLista;
  private String lapide;
  private long posiArqPrinc;

  public listainvertida() {
    nomeLista = null;
    posiArqPrinc = 0;
    lapide = " ";
  }

  public void setNomeLista(String nomeLista) {
    this.nomeLista = nomeLista;
  }

  public void setPosiArqPrinc(long posiArqPrinc) {
    this.posiArqPrinc = posiArqPrinc;
  }

  public void setLapide(String lapide) {
    this.lapide = lapide;
  }

  public String getNomeLista() {
    return nomeLista;
  }

  public long getPosiArqPrinc() {
    return posiArqPrinc;
  }

  public String getLapide() {
    return lapide;
  }

  // conferir esses pesquisaListaInvertida e desmembrar
  public long[] pesquisaListaInvertida(String n) {
    String concat = "0";
    long posicoesLI[];
    posicoesLI = new long[1];
    boolean marcador = true;
    try {

      RandomAccessFile arq = new RandomAccessFile("src/database/listainvertida.db", "rw");
      long variavelContador = 0;

      while (variavelContador < arq.length()) {

        String lerdoArq = arq.readUTF();

        if (lerdoArq.equals(n)) {

          long pegarOsIndices = 0;
          String convert;
          pegarOsIndices = arq.readLong();

          while (pegarOsIndices != 0) {
            concat = "";
            convert = Long.toString(pegarOsIndices);
            concat += convert;
            pegarOsIndices = arq.readLong();
          }
          marcador = false;
        }

        if (marcador) {
          arq.seek(400);
          variavelContador = arq.getFilePointer();
        }
        variavelContador = arq.getFilePointer();
      }
      posicoesLI = desmembrarStringListaInvertida(concat);
      arq.close();
    } catch (Exception e) {
      System.out.println("Erro na pesquisa na lista invertida: " + e.getCause());

    }
    return posicoesLI;
  }

  private long[] desmembrarStringListaInvertida(String n) {
    long posicoesLI[];
    posicoesLI = new long[1];
    if (!(n.equals("0"))) {

      String receberStr = getNomeLista();
      int qtdelementos = receberStr.length() / 8;
      posicoesLI = new long[qtdelementos - 1];

      String concat = "";
      int contadorMandarProarray = 0;
      long convertStringtoLong = 0;
      for (int i = 0; i < receberStr.length(); i++) {

        char letra = receberStr.charAt(i);
        concat += letra;

        if ((i % 8) == 0) {
          convertStringtoLong = Long.valueOf(concat);
          posicoesLI[contadorMandarProarray] = convertStringtoLong;
          concat = "";
        }

      }

    } else {
      System.out.println("Não existe elementos na string");
    }
    return posicoesLI;
  }

  public long pesquisaListaInvertidaParaoCreate(String n) {

    long posicoesLI = -1;
    long posiAntesdoReadString = 0;
    boolean marcador = true;
    try {

      RandomAccessFile arq = new RandomAccessFile("src/database/listainvertida.db", "rw");
      long variavelContador = 0;

      while (variavelContador < arq.length()) {
        posiAntesdoReadString = arq.getFilePointer();
        String lerdoArq = arq.readUTF();

        if (lerdoArq.equals(n)) {

          long pegarOsIndices = 0;
          pegarOsIndices = arq.readLong();

          if (pegarOsIndices != 0) {
            posicoesLI = posiAntesdoReadString;
            variavelContador = arq.length();
          }
          marcador = false;
        }

        if (marcador) {
          arq.seek(400);

        }

        variavelContador = arq.getFilePointer();
      }

      arq.close();
    } catch (Exception e) {
      System.out.println("Erro na pesquisa na lista invertida: " + e.getCause());

    }
    return posicoesLI;
  }

  public void escreverListaInvertida(String nome, String lap) {

    try {
      RandomAccessFile arq = new RandomAccessFile("src/database/listainvertida.db", "rw");
      long posicoesLI;

      if (arq.length() != 0) {

        posicoesLI = pesquisaListaInvertidaParaoCreate(nome);

        if (posicoesLI != -1) {
          long salvarPosicaoDeEscrita = 0;
          arq.seek(posicoesLI);
          String pegarNome;
          pegarNome = arq.readUTF();

          if (pegarNome.equals(nome)) {
            boolean verificador = true;
            while (verificador) {

              long lerPosi = 0;

              lerPosi = arq.readLong();

              if (lerPosi == 0) {
                salvarPosicaoDeEscrita = arq.getFilePointer() - 8;
                verificador = false;
              }

            }

          }

          arq.seek(salvarPosicaoDeEscrita);
          arq.writeLong(getPosiArqPrinc());
          arq.writeUTF(getLapide());
        } else {

          arq.seek(arq.length());
          arq.writeUTF(getNomeLista());
          arq.writeLong(getPosiArqPrinc());
          arq.writeUTF(getLapide());

        }
      } else {

        arq.seek(0);
        arq.writeUTF(getNomeLista());
        arq.writeLong(getPosiArqPrinc());
        arq.writeUTF(getLapide());

      }
      arq.close();
    } catch (Exception e) {
      System.out.println("Erro no escreverListaInvertida: " + e.getCause());
      return;
    }

  }
}
