import java.io.RandomAccessFile;

public class listainvertida {

  private String nomeLista;
  private long posiArqPrinc;

  public listainvertida() {
    nomeLista = null;
    posiArqPrinc = 0;

  }

  public void setNomeLista(String nomeLista) {
    this.nomeLista = nomeLista;
  }

  public void setPosiArqPrinc(long posiArqPrinc) {
    this.posiArqPrinc = posiArqPrinc;
  }

  public String getNomeLista() {
    return nomeLista;
  }

  public long getPosiArqPrinc() {
    return posiArqPrinc;
  }

  // funcoes abaixo referente a lista invertida

  private void imprimirListaInvertida(long l[]) {

    fut ft = new fut();

    try {

      RandomAccessFile arq = new RandomAccessFile("src/database/futebol.db", "rw");
      long recebervalordeLongdoArray = 0;
      String lapide = "*";
      for (int i = 0; i < l.length; i++) {

        recebervalordeLongdoArray = l[i];

        arq.seek(recebervalordeLongdoArray + 6);
        String lerLapide = arq.readUTF();
        if (lerLapide.equals(lapide)) {
          System.out.println("Arquivo NÃO existe !!!");
        } else {

          arq.seek(recebervalordeLongdoArray + 4);

          short Id = arq.readShort();

          ft.setIdClube(Id);

          lerLapide = arq.readUTF();
          ft.setLapide(lerLapide);

          String nome = arq.readUTF();
          ft.setNome(nome);

          String cnpj = arq.readUTF();
          ft.setCnpj(cnpj);

          String cidade = arq.readUTF();
          ft.setCidade(cidade);

          byte partidasJogadas = arq.readByte();
          ft.setPartidasJogadas(partidasJogadas);

          byte pontos = arq.readByte();
          ft.setPontos(pontos);

          System.out.println(ft.toString());

          ft = new fut();
        }

      }

      arq.close();
    } catch (Exception e) {
      System.out.println("Error em imprimirListaInvertidaL: " + e.getCause());
    }

  }

  private long[] desmembrarStringListaInvertida(String n, int qtdElementos) {
    long posicoesLI[];
    posicoesLI = new long[qtdElementos];
    int contadordePeV = 0;
    if (!(n.equals("0"))) {
      String receberNumero = "";
      for (int i = 0; i < n.length(); i++) {

        char letra = n.charAt(i);

        if (letra != ';') {
          String letrastr = "" + letra;
          receberNumero = receberNumero.concat(letrastr);
        } else {

          long convertStr = 0;
          convertStr = Long.parseLong(receberNumero, 10);
          posicoesLI[contadordePeV] = convertStr;
          contadordePeV++;
          receberNumero = "";

        }

      }

    } else {
      System.out.println("Não existe elementos na string");
    }
    return posicoesLI;
  }

  // --------------------------------------
  // Os dois métodos acima que são o imprimirListaInvertida e o
  // desmembrarStrngListaInvertida são métodos suporte ao pesquisaListaInvertida,
  // na qual recebe um nome ou cidade, e pesquisa sua exata posicao na lista
  // invertida, caso não seja marcado para impressão, a funcao retorna o valor
  // antes de ler o nome do registro, caso seja para imprimir, o programa
  // concatena todas as posicoes ali presente separadas por ponto e virgula (;),
  // mandando para o desmembrar que tranforma todos os registros na string em um
  // array de long que manda para o imprimir que imprime os registros nessa
  // posição.
  // --------------------------------------

  public long pesquisaListaInvertida(String n, boolean eParaImprimir) {

    long retornarPosicao = -1;

    String concat = "0";
    long posicoesLI[];

    boolean marcador = true;
    boolean sairdoLoopPrincipal = true;
    long posiDepoisdoReadString = 0;
    long posiAntesdoReadLong = 0;
    int qtdElementos = 0;
    try {

      RandomAccessFile arq = new RandomAccessFile("src/database/listainvertida.db", "rw");
      long variavelContador = 0;

      while (variavelContador < arq.length() && sairdoLoopPrincipal) {
        posiAntesdoReadLong = arq.getFilePointer();
        String lerdoArq = arq.readUTF();
        posiDepoisdoReadString = arq.getFilePointer();

        if (lerdoArq.equals(n)) {
          retornarPosicao = posiAntesdoReadLong;

          if (eParaImprimir) {

            long pegarOsIndices = 0;
            String convert;
            pegarOsIndices = arq.readLong();
            if (pegarOsIndices != 0) {
              concat = "";
            }

            while (pegarOsIndices != -10) {
              if (pegarOsIndices != 0) {
                qtdElementos++;
                convert = Long.toString(pegarOsIndices);
                concat = concat.concat(convert + ";");
              }

              pegarOsIndices = arq.readLong();
            }
          }
          marcador = false;
          sairdoLoopPrincipal = false;
          variavelContador = arq.length();
        }

        if (marcador) {
          arq.seek(posiDepoisdoReadString + 168);
        }
        variavelContador = arq.getFilePointer();
      }

      if (!(concat.equals("0"))) {
        posicoesLI = desmembrarStringListaInvertida(concat, qtdElementos);
        imprimirListaInvertida(posicoesLI);
      }

      arq.close();
    } catch (Exception e) {
      System.out.println("Erro na pesquisa na lista invertida: " + e.getCause());

    }

    return retornarPosicao;
  }

  public long pesquisaListaInvertidaParaoCreate(String n) {// Aqui retorna posicao no arquivo da lista
                                                           // invertida

    long posicoesLI = -1;

    long posiDepoisdoReadString = 0;
    long posiAntesdoReadLong = 0;
    boolean marcador = true;
    boolean marcadorLoop1 = true;
    try {

      RandomAccessFile arq = new RandomAccessFile("src/database/listainvertida.db", "rw");
      long variavelContador = 0;

      while (variavelContador < arq.length() && marcadorLoop1) {
        String lerdoArq = "";
        if (arq.getFilePointer() != 0) {

          lerdoArq = arq.readUTF();

        } else {
          lerdoArq = arq.readUTF();
        }

        posiDepoisdoReadString = arq.getFilePointer();

        if (lerdoArq.equals(n)) {
          boolean marcador2 = true;
          long pegarOsIndices = 0;
          while (pegarOsIndices != -10 && marcador2) {
            posiAntesdoReadLong = arq.getFilePointer();
            pegarOsIndices = arq.readLong();

            if (pegarOsIndices == 0) {
              posicoesLI = posiAntesdoReadLong;
              variavelContador = arq.length();
              marcador2 = false;
              marcadorLoop1 = false;
            } else {
              if (pegarOsIndices == -10) {
                marcador2 = false;
                System.out.println("Não foi implementado na lista, lista cheia !");
              }
            }
          }

          marcador = false;
        }

        if (marcador) {

          arq.seek(posiDepoisdoReadString + 168);

        }

        variavelContador = arq.getFilePointer();
      }

      arq.close();
    } catch (Exception e) {
      System.out.println("Erro na pesquisa na lista invertida: " + e.getCause());

    }
    return posicoesLI;
  }

  public void completar20casa(long posi) {
    try {
      RandomAccessFile arq = new RandomAccessFile("src/database/listainvertida.db", "rw");

      arq.seek(posi);
      long zero = 0;
      for (int i = 0; i < 19; i++) {

        arq.writeLong(zero);

      }
      arq.writeLong(-10);
      arq.close();
    } catch (Exception e) {
      System.out.println("Error completar20Casa: " + e.getCause());
    }
  }

  // --------------------------------------
  // O método escreverListaInvertida avalia 2 pontos para o write na lista, se a
  // lista está vazia, e se o elemento ja está presente na lista,
  // --------------------------------------
  public void escreverListaInvertida(String nome) {

    try {
      RandomAccessFile arq = new RandomAccessFile("src/database/listainvertida.db", "rw");
      long posicoesLI;

      if (arq.length() != 0) {

        posicoesLI = pesquisaListaInvertidaParaoCreate(nome);

        if (posicoesLI != -1) {

          arq.seek(posicoesLI);
          arq.writeLong(getPosiArqPrinc());

        } else {

          arq.seek(arq.length());
          String receberNomeLista = getNomeLista();
          arq.writeUTF(receberNomeLista);
          arq.writeLong(getPosiArqPrinc());
          completar20casa(arq.getFilePointer());

        }
      } else {

        arq.seek(0);
        String receberNomeLista = getNomeLista();
        arq.writeUTF(receberNomeLista);
        arq.writeLong(getPosiArqPrinc());
        completar20casa(arq.getFilePointer());

      }
      arq.close();
    } catch (Exception e) {
      System.out.println("Erro no escreverListaInvertida: " + e.getCause());
      return;
    }

  }

}
