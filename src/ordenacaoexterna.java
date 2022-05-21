import java.io.*;

public class ordenacaoexterna {

  private static int qtdElementoArrayIndice(indice[] a) {
    // essa funcao pega a quantidade de elementos presente no array de objeto
    // indice, foi construida com o objetivo de gerar o número do elemento mais a
    // direita quando o vetor não está completo
    int contador = 0;

    for (int i = 0; i < a.length; i++) {

      if (a[i] != null) {
        contador++;
      }

    }
    return contador;
  }

  private static void limparArquivoDoZeros(long tamArq) {
    int convertLong = (int) tamArq;
    byte b[] = new byte[convertLong];
    try {
      RandomAccessFile arq = new RandomAccessFile("src/database/aindices.db", "rw");

      arq.readFully(b);
      arquivocrud arqcru = new arquivocrud();
      arqcru.deletaTudo(-1, -1, -1, -1, -1, 1, -1);
      arq.seek(0);
      arq.write(b);
      arq.close();
    } catch (Exception e) {
      System.out.println("Error no bloco limparArquivoDoZeros da OE: " + e.getCause());
    }
  }

  // --------------------------------------
  // O método limparArquivodoZeros é um método de apoio para o método de
  // corrigirArquivoIndice e esse método corrigir tem a função de eleminar os
  // zeros peridos no meio do arquivo de indices, e quando ele salva todo o
  // arquivo evitando os zeros em um array de bytes, a função limparArquivoDoZeros
  // apaga o arquivo e salva o array de byte por cima, retirando os zeros
  // --------------------------------------

  public void corrigirArquivoIndice() {
    // essa explicacao agora tem que ser feita pois nao existe registro comecando em
    // 0 e sim em 1
    // 0_________1___"0"____2________3_________4
    // Para embaralhar e a ordenacao fazer sentido eu mudei a ordem de escrita,
    // então se escreve primeiros números impares e depois numeros pares, porém já
    // que sempre gera o indice par primeiro ele salva 13 posicao na frente da posi
    // 0
    // para deixar o espaço para o numero par, porém quando fica um número impar de
    // elementos como no exemplo acima, fica um espaço de 0 no meio do arquivo, essa
    // funcao copia o ultimo registro para esse espaço e deixa o 0 pro fim do
    // arquivo, porém ainda continua com o 0, e ela retorna um boolean para o método
    // de ordenacao, para ele saber, quando ele deve ignorar as ultimas 13 casas que
    // seria um registro zerado (quando for impar), e quando ele considera esses 13
    // bytes finais (quando o número de elementos no arquivo for par).
    // funcao tem objetivo de tirar o gap de 0 entre os registros
    try {

      RandomAccessFile arq = new RandomAccessFile("src/database/aindices.db", "rw");

      if (arq.length() > 13) {

        arq.seek(arq.length() - 24);

        long lerLong = arq.readLong();

        byte[] zerar = new byte[12];
        if (lerLong == 0) {

          indice indd = new indice();
          arq.seek(arq.length() - 13);
          indd.setIdIndice(arq.readShort());
          indd.setPosiIndice(arq.readLong());
          indd.setLapide(arq.readUTF());

          arq.seek(arq.length() - 26);

          arq.writeShort(indd.getIdIndice());
          arq.writeLong(indd.getPosiIndice());
          arq.writeUTF(indd.getLapide());

          arq.seek((arq.length() - 12));
          arq.write(zerar);

          arq.seek(arq.length() - 13);
          long salvarFinalDoArquivo = arq.getFilePointer();

          limparArquivoDoZeros(salvarFinalDoArquivo);

        }

      }

      arq.close();

    } catch (Exception e) {
      System.out.println("Aconteceu um error ao corrigir o arquivo de dados: " + e.getMessage());
    }

  }

  // FUNCOES DE APOIO ORDENACAO EXTERNA

  // --------------------------------------
  // Nessa parte, foi realizada uma distruibuição dos registros em 10 caminhos, e
  // depois feito a ordenação externa, dessa forma as funções que ordenam do
  // arquivo 1 e 2 para 3 e 4, são funções de apoio para o método ordenação
  // externa, e quando acaba tudo ele efetua, ordernarToArqIndice, que ele pega o
  // ultimo arquivo que foi feito alguma escrita, e copia para o arquivo
  // aindices.db que é o arquivo de indices principal
  // --------------------------------------

  public static boolean ordernar1e2para3(RandomAccessFile arq1, RandomAccessFile arq2, RandomAccessFile arq3,
      int tamCaminho, int contadorPRINCIPAL) {
    boolean ultimoSavearq3 = false;
    try {
      indice ic = new indice();
      indice ic2 = new indice();

      int contadorDeComparacoes = 0;
      int contardorPonteiroArq1 = 0;
      int contardorPonteiroArq2 = 0;

      boolean podeLerArq1 = false;
      boolean podeLerArq2 = false;

      long tamArq1 = arq1.length();
      long tamArq2 = arq2.length();

      int tamanhoArq1Inteiro = (int) tamArq1;
      int tamanhoArq2Inteiro = (int) tamArq2;

      tamanhoArq1Inteiro /= 13;
      tamanhoArq2Inteiro /= 13;

      short valor1Arq1 = 0;
      short valor2Arq2 = 0;

      int testarArq1 = tamanhoArq1Inteiro - tamCaminho;
      int testarArq2 = tamanhoArq2Inteiro - tamCaminho;
      if (testarArq1 >= 0) {
        podeLerArq1 = true;
      }
      if ((testarArq2 + tamanhoArq1Inteiro) > 0) {
        podeLerArq2 = true;
      }

      if (podeLerArq1 && podeLerArq2) {

        while (contadorDeComparacoes < tamCaminho) {

          if (podeLerArq1) {
            // leu o registro do 1 arquivo
            ic.setIdIndice(arq1.readShort());
            ic.setPosiIndice(arq1.readLong());
            ic.setLapide(arq1.readUTF());
            valor1Arq1 = ic.getIdIndice();

          }

          if (podeLerArq2) {
            // leu o registro do 2 arquivo

            ic2.setIdIndice(arq2.readShort());
            valor2Arq2 = ic2.getIdIndice();
            ic2.setPosiIndice(arq2.readLong());
            ic2.setLapide(arq2.readUTF());

          }

          // fazer a comparacao

          if (valor2Arq2 < valor1Arq1) {

            arq3.writeShort(ic2.getIdIndice());
            arq3.writeLong(ic2.getPosiIndice());
            arq3.writeUTF(ic2.getLapide());
            podeLerArq1 = false;
            podeLerArq2 = true;
            contardorPonteiroArq2++;

          } else {
            arq3.writeShort(ic.getIdIndice());
            arq3.writeLong(ic.getPosiIndice());
            arq3.writeUTF(ic.getLapide());
            podeLerArq1 = true;
            podeLerArq2 = false;
            contardorPonteiroArq1++;

          }
          contadorDeComparacoes++;

        }

        // caso o primeiro caminho esta completo e o segundo incompleto
        if (contardorPonteiroArq1 != tamCaminho) {
          long tamArq3 = arq3.length();
          arq3.seek(tamArq3);
          int tamanhoCaminhoIncompleto1 = (int) arq1.length();
          tamanhoCaminhoIncompleto1 /= 13;

          if (tamanhoCaminhoIncompleto1 > tamCaminho) {

            if (tamanhoCaminhoIncompleto1 != tamCaminho
                && (tamanhoCaminhoIncompleto1 < (tamCaminho * contadorPRINCIPAL))) {
              tamanhoCaminhoIncompleto1 = tamanhoCaminhoIncompleto1 - (tamCaminho * (contadorPRINCIPAL - 1));
            } else {
              tamanhoCaminhoIncompleto1 = tamCaminho;
            }

          }
          while (contardorPonteiroArq1 < (tamanhoCaminhoIncompleto1)) {
            arq3.writeShort(ic.getIdIndice());
            arq3.writeLong(ic.getPosiIndice());
            arq3.writeUTF(ic.getLapide());
            contardorPonteiroArq1++;
            if (contardorPonteiroArq1 < (tamanhoCaminhoIncompleto1)) {
              ic.setIdIndice(arq1.readShort());
              ic.setPosiIndice(arq1.readLong());
              ic.setLapide(arq1.readUTF());
            }

          }

        } else if (contardorPonteiroArq2 != tamCaminho) {
          int tamanhoCaminhoIncompleto2 = (int) arq2.length();

          tamanhoCaminhoIncompleto2 /= 13;
          if (tamanhoCaminhoIncompleto2 > tamCaminho) {
            if (tamanhoCaminhoIncompleto2 != tamCaminho
                && (tamanhoCaminhoIncompleto2 < (tamCaminho * contadorPRINCIPAL))) {
              tamanhoCaminhoIncompleto2 = tamanhoCaminhoIncompleto2 - (tamCaminho * (contadorPRINCIPAL - 1));

            } else {
              tamanhoCaminhoIncompleto2 = tamCaminho;
            }
          }

          long tamArq3 = arq3.length();
          arq3.seek(tamArq3);
          while (contardorPonteiroArq2 < (tamanhoCaminhoIncompleto2)) {
            arq3.writeShort(ic2.getIdIndice());
            arq3.writeLong(ic2.getPosiIndice());
            arq3.writeUTF(ic2.getLapide());
            contardorPonteiroArq2++;
            if (contardorPonteiroArq2 < (tamanhoCaminhoIncompleto2)) {
              ic2.setIdIndice(arq2.readShort());
              ic2.setPosiIndice(arq2.readLong());
              ic2.setLapide(arq2.readUTF());
            }

          }
        }
        ultimoSavearq3 = true;
      } else {
        // caso o arquivo 1 tenha dado e o 2 não.
        int qtdElementosNoCaminho = 0;

        qtdElementosNoCaminho = tamanhoArq1Inteiro;

        int novoContador = 0;
        while (novoContador < qtdElementosNoCaminho) {

          ic.setIdIndice(arq1.readShort());
          ic.setPosiIndice(arq1.readLong());
          ic.setLapide(arq1.readUTF());

          arq3.writeShort(ic.getIdIndice());
          arq3.writeLong(ic.getPosiIndice());
          arq3.writeUTF(ic.getLapide());

          novoContador++;
        }
        ultimoSavearq3 = true;
      }

    } catch (Exception e) {
      System.out.println("Error acontecendo no Ordernar1e2para3: " + e.getMessage());
      return false;
    }
    return ultimoSavearq3;
  }

  public static boolean ordernar1e2para4(RandomAccessFile arq1, RandomAccessFile arq2, RandomAccessFile arq4,
      int tamCaminho, int contadorPRINCIPAL) {

    boolean ultimoSavearq4 = false;

    try {

      indice ic = new indice();
      indice ic2 = new indice();

      int contadorDeComparacoes = 0;
      int contardorPonteiroArq1 = 0;
      int contardorPonteiroArq2 = 0;

      boolean podeLerArq1 = false;
      boolean podeLerArq2 = false;

      long tamArq1 = arq1.length();
      long tamArq2 = arq2.length();

      short valor1Arq1 = 0;
      short valor2Arq2 = 0;

      int tamanhoArq1Inteiro = (int) tamArq1;
      int tamanhoArq2Inteiro = (int) tamArq2;
      tamanhoArq1Inteiro /= 13;
      tamanhoArq2Inteiro /= 13;
      tamArq2 /= 13;
      int testarArq1 = tamanhoArq1Inteiro - tamCaminho;
      int testarArq2 = tamanhoArq2Inteiro - tamCaminho;

      if (testarArq1 >= 0) {
        podeLerArq1 = true;
      }
      if (testarArq2 > 0) {
        podeLerArq2 = true;
      }

      if (podeLerArq1 && podeLerArq2) {
        // arq1.seek(0);
        // arq2.seek(0);
        // long arq2seek = (long) tamCaminho;
        // arq2.seek(arq2seek * 13);
        // System.out.println(arq2.getFilePointer());
        while (contadorDeComparacoes < tamCaminho) {

          if (podeLerArq1) {
            // leu o registro do 1 arquivo
            ic.setIdIndice(arq1.readShort());
            ic.setPosiIndice(arq1.readLong());
            ic.setLapide(arq1.readUTF());
            valor1Arq1 = ic.getIdIndice();

          }

          if (podeLerArq2) {
            // leu o registro do 2 arquivo

            ic2.setIdIndice(arq2.readShort());
            valor2Arq2 = ic2.getIdIndice();
            ic2.setPosiIndice(arq2.readLong());
            ic2.setLapide(arq2.readUTF());

          }

          if (valor2Arq2 < valor1Arq1) {

            arq4.writeShort(ic2.getIdIndice());
            arq4.writeLong(ic2.getPosiIndice());
            arq4.writeUTF(ic2.getLapide());
            podeLerArq1 = false;
            podeLerArq2 = true;
            contardorPonteiroArq2++;

          } else {
            arq4.writeShort(ic.getIdIndice());
            arq4.writeLong(ic.getPosiIndice());
            arq4.writeUTF(ic.getLapide());
            podeLerArq1 = true;
            podeLerArq2 = false;
            contardorPonteiroArq1++;

          }

          contadorDeComparacoes++;

        }

        // caso o arquivo dois nao seja completo (imcompleto)

        if (contardorPonteiroArq1 != tamCaminho) {
          long tamArq4 = arq4.length();
          arq4.seek(tamArq4);
          int tamanhoCaminhoIncompleto1 = (int) arq1.length();
          tamanhoCaminhoIncompleto1 /= 13;

          if (tamanhoCaminhoIncompleto1 > tamCaminho) {

            if (tamanhoCaminhoIncompleto1 != tamCaminho && (tamanhoArq2Inteiro < (tamCaminho * contadorPRINCIPAL))) {
              tamanhoCaminhoIncompleto1 = tamanhoCaminhoIncompleto1 - (tamCaminho * (contadorPRINCIPAL - 1));
              ;
            } else {
              tamanhoCaminhoIncompleto1 = tamCaminho;
            }
          }
          while (contardorPonteiroArq1 < (tamanhoCaminhoIncompleto1)) {
            arq4.writeShort(ic.getIdIndice());
            arq4.writeLong(ic.getPosiIndice());
            arq4.writeUTF(ic.getLapide());
            contardorPonteiroArq1++;
            if (contardorPonteiroArq1 < (tamanhoCaminhoIncompleto1)) {
              ic.setIdIndice(arq1.readShort());
              ic.setPosiIndice(arq1.readLong());
              ic.setLapide(arq1.readUTF());
            }

          }

        } else if (contardorPonteiroArq2 != tamCaminho) {

          long tamArq4 = arq4.length();
          arq4.seek(tamArq4);
          int tamanhoCaminhoIncompleto2 = (int) arq2.length();
          tamanhoCaminhoIncompleto2 /= 13;

          if (tamanhoCaminhoIncompleto2 > tamCaminho) {

            if (tamanhoCaminhoIncompleto2 != tamCaminho && (tamanhoArq2Inteiro < (tamCaminho * contadorPRINCIPAL))) {
              tamanhoCaminhoIncompleto2 = tamanhoCaminhoIncompleto2 - (tamCaminho * (contadorPRINCIPAL - 1));
            } else {
              tamanhoCaminhoIncompleto2 = tamCaminho;
            }
          }
          while (contardorPonteiroArq2 < (tamanhoCaminhoIncompleto2)) {
            arq4.writeShort(ic2.getIdIndice());
            arq4.writeLong(ic2.getPosiIndice());
            arq4.writeUTF(ic2.getLapide());
            contardorPonteiroArq2++;
            if (contardorPonteiroArq2 < (tamanhoCaminhoIncompleto2)) {
              ic2.setIdIndice(arq2.readShort());
              ic2.setPosiIndice(arq2.readLong());
              ic2.setLapide(arq2.readUTF());
            }

          }
        }
      } else {// caso o arquivo 1 seja imcompleto
        int qtdElementosNoCaminho = 0;

        qtdElementosNoCaminho = (tamanhoArq1Inteiro - tamCaminho);

        int novoCont = 0;
        while (novoCont < qtdElementosNoCaminho) {

          ic.setIdIndice(arq1.readShort());
          ic.setPosiIndice(arq1.readLong());
          ic.setLapide(arq1.readUTF());

          arq4.writeShort(ic.getIdIndice());
          arq4.writeLong(ic.getPosiIndice());
          arq4.writeUTF(ic.getLapide());

          novoCont++;
        }

        ultimoSavearq4 = true;

      }
      ultimoSavearq4 = true;
    } catch (Exception e) {
      ultimoSavearq4 = false;
      System.out.println("Error no método ordernar1e2para4: " + e.getMessage());
      return ultimoSavearq4;
    }

    return ultimoSavearq4;
  }

  public static boolean ordernar3e4para1(RandomAccessFile arq1, RandomAccessFile arq3, RandomAccessFile arq4,
      int tamCaminho, int contadorPRINCIPAL) {

    boolean ultimoSalveArq1 = false;
    try {

      indice ic = new indice();
      indice ic2 = new indice();

      int contadorDeComparacoes = 0;
      int contardorPonteiroArq3 = 0;
      int contardorPonteiroArq4 = 0;

      boolean podeLerArq3 = false;
      boolean podeLerArq4 = false;

      long tamArq3 = arq3.length();
      long tamArq4 = arq4.length();

      short valor1Arq3 = 0;
      short valor2Arq4 = 0;

      int tamanhoArq3Inteiro = (int) tamArq3;
      int tamanhoArq4Inteiro = (int) tamArq4;

      tamanhoArq3Inteiro /= 13;
      tamanhoArq4Inteiro /= 13;

      int testarArq3 = tamanhoArq3Inteiro - tamCaminho;
      int testarArq4 = tamanhoArq4Inteiro - tamCaminho;

      if (testarArq3 >= 0) {
        podeLerArq3 = true;
      }
      if ((testarArq4 + tamanhoArq3Inteiro) > 0) {
        podeLerArq4 = true;
      }
      if (podeLerArq3 && podeLerArq4) {

        arq3.seek(0);
        arq4.seek(0);

        while (contadorDeComparacoes < tamCaminho) {
          if (podeLerArq3) {
            // leu o registro do 3 arquivo
            ic.setIdIndice(arq3.readShort());
            ic.setPosiIndice(arq3.readLong());
            ic.setLapide(arq3.readUTF());
            valor1Arq3 = ic.getIdIndice();

          }

          if (podeLerArq4) {
            // leu o registro do 4 arquivo

            ic2.setIdIndice(arq4.readShort());
            valor2Arq4 = ic2.getIdIndice();
            ic2.setPosiIndice(arq4.readLong());
            ic2.setLapide(arq4.readUTF());

          }

          if (valor2Arq4 < valor1Arq3) {

            arq1.writeShort(ic2.getIdIndice());
            arq1.writeLong(ic2.getPosiIndice());
            arq1.writeUTF(ic2.getLapide());
            podeLerArq3 = false;
            podeLerArq4 = true;
            contardorPonteiroArq4++;

          } else {
            arq1.writeShort(ic.getIdIndice());
            arq1.writeLong(ic.getPosiIndice());
            arq1.writeUTF(ic.getLapide());
            podeLerArq3 = true;
            podeLerArq4 = false;
            contardorPonteiroArq3++;

          }
          contadorDeComparacoes++;

        }
        // caso o arquivo 3 esteja completo e o arquivo 4 esteja incompleto
        if (contardorPonteiroArq3 != tamCaminho) {
          long tamArq1 = arq1.length();
          arq1.seek(tamArq1);
          int tamanhoCaminhoIncompleto3 = (int) arq3.length();
          tamanhoCaminhoIncompleto3 /= 13;

          if (tamanhoCaminhoIncompleto3 > tamCaminho) {

            if (tamanhoCaminhoIncompleto3 != tamCaminho
                && (tamanhoCaminhoIncompleto3 < (tamCaminho * contadorPRINCIPAL))) {
              tamanhoCaminhoIncompleto3 = tamanhoCaminhoIncompleto3 - (tamCaminho * (contadorPRINCIPAL - 1));

            } else {
              tamanhoCaminhoIncompleto3 = tamCaminho;
            }
          }
          while (contardorPonteiroArq3 < (tamanhoCaminhoIncompleto3)) {
            arq1.writeShort(ic.getIdIndice());
            arq1.writeLong(ic.getPosiIndice());
            arq1.writeUTF(ic.getLapide());
            contardorPonteiroArq3++;
            if (contardorPonteiroArq3 < (tamanhoCaminhoIncompleto3)) {
              ic.setIdIndice(arq3.readShort());
              ic.setPosiIndice(arq3.readLong());
              ic.setLapide(arq3.readUTF());
            }

          }

        } else if (contardorPonteiroArq4 != tamCaminho) {

          long tamArq1 = arq1.length();
          arq1.seek(tamArq1);
          int tamanhoCaminhoIncompleto4 = (int) arq4.length();
          tamanhoCaminhoIncompleto4 /= 13;

          if (tamanhoCaminhoIncompleto4 > tamCaminho) {

            if (tamanhoCaminhoIncompleto4 != tamCaminho
                && (tamanhoCaminhoIncompleto4 < (tamCaminho * contadorPRINCIPAL))) {
              tamanhoCaminhoIncompleto4 = tamanhoCaminhoIncompleto4 - (tamCaminho * (contadorPRINCIPAL - 1));
              tamanhoCaminhoIncompleto4++;
            } else {
              tamanhoCaminhoIncompleto4 = tamCaminho;
            }
          }

          while (contardorPonteiroArq4 < (tamanhoCaminhoIncompleto4)) {
            arq1.writeShort(ic2.getIdIndice());
            arq1.writeLong(ic2.getPosiIndice());
            arq1.writeUTF(ic2.getLapide());
            contardorPonteiroArq4++;
            if (contardorPonteiroArq4 < (tamanhoCaminhoIncompleto4)) {
              ic2.setIdIndice(arq4.readShort());
              ic2.setPosiIndice(arq4.readLong());
              ic2.setLapide(arq4.readUTF());
            }

          }
        }
        ultimoSalveArq1 = true;
      } else {// caso o arquivo 3 esteja incompleto e nao exista registro em arq 4.
        int qtdElementosNoCaminho = 0;

        qtdElementosNoCaminho = tamanhoArq3Inteiro;

        int novoContador = 0;
        while (novoContador < qtdElementosNoCaminho) {

          ic.setIdIndice(arq3.readShort());
          ic.setPosiIndice(arq3.readLong());
          ic.setLapide(arq3.readUTF());

          arq1.writeShort(ic.getIdIndice());
          arq1.writeLong(ic.getPosiIndice());
          arq1.writeUTF(ic.getLapide());

          novoContador++;
        }
        ultimoSalveArq1 = true;
      }

    } catch (Exception e) {
      System.out.println("Aconteceu um erro no método ordernar3e4para1: " + e.getMessage());
      return false;
    }

    return ultimoSalveArq1;
  }

  public static boolean ordernar3e4para2(RandomAccessFile arq2, RandomAccessFile arq3, RandomAccessFile arq4,
      int tamCaminho, int contadorPRINCIPAL) {

    boolean ultimoSavearq2 = false;

    try {

      indice ic = new indice();
      indice ic2 = new indice();

      int contadorDeComparacoes = 0;
      int contardorPonteiroArq3 = 0;
      int contardorPonteiroArq4 = 0;

      boolean podeLerArq3 = false;
      boolean podeLerArq4 = false;

      long tamArq3 = arq3.length();
      long tamArq4 = arq4.length();

      short valor1Arq3 = 0;
      short valor2Arq4 = 0;

      int tamanhoArq3Inteiro = (int) tamArq3;
      int tamanhoArq4Inteiro = (int) tamArq4;
      tamanhoArq3Inteiro /= 13;
      tamanhoArq4Inteiro /= 13;
      tamArq3 /= 13;

      int testarArq3 = tamanhoArq3Inteiro - tamCaminho;
      int testarArq4 = tamanhoArq4Inteiro - tamCaminho;

      if (testarArq3 >= 0) {
        podeLerArq3 = true;
      }
      if (testarArq4 > 0) {
        podeLerArq4 = true;
      }

      if (podeLerArq3 && podeLerArq4) {

        while (contadorDeComparacoes < tamCaminho) {
          if (podeLerArq3) {
            // leu o registro do 1 arquivo
            ic.setIdIndice(arq3.readShort());
            ic.setPosiIndice(arq3.readLong());
            ic.setLapide(arq3.readUTF());
            valor1Arq3 = ic.getIdIndice();

          }

          if (podeLerArq4) {
            // leu o registro do 2 arquivo

            ic2.setIdIndice(arq4.readShort());
            valor2Arq4 = ic2.getIdIndice();
            ic2.setPosiIndice(arq4.readLong());
            ic2.setLapide(arq4.readUTF());

          }

          // fazer a comparacao

          if (valor2Arq4 < valor1Arq3) {

            arq2.writeShort(ic2.getIdIndice());
            arq2.writeLong(ic2.getPosiIndice());
            arq2.writeUTF(ic2.getLapide());
            podeLerArq3 = false;
            podeLerArq4 = true;
            contardorPonteiroArq4++;

          } else {
            arq2.writeShort(ic.getIdIndice());
            arq2.writeLong(ic.getPosiIndice());
            arq2.writeUTF(ic.getLapide());
            podeLerArq3 = true;
            podeLerArq4 = false;
            contardorPonteiroArq3++;

          }

          contadorDeComparacoes++;

        }
        // caso agora o arquivo 4 nao esteja completo e o 3 sim

        if (contardorPonteiroArq3 != tamCaminho) {// caso1
          long tamArq2 = arq2.length();
          int qtdElementosarq2 = (int) tamArq2 / 13;

          if (qtdElementosarq2 > tamCaminho) {

            if (qtdElementosarq2 != tamCaminho && (qtdElementosarq2 < (tamCaminho * contadorPRINCIPAL))) {
              qtdElementosarq2 = tamCaminho - (contadorPRINCIPAL - 1);

            } else {
              qtdElementosarq2 = tamCaminho;
            }
          }
          arq2.seek(tamArq2);
          while (contardorPonteiroArq3 < (qtdElementosarq2)) {
            arq2.writeShort(ic.getIdIndice());
            arq2.writeLong(ic.getPosiIndice());
            arq2.writeUTF(ic.getLapide());
            contardorPonteiroArq3++;
            if (contardorPonteiroArq3 < (qtdElementosarq2)) {
              ic.setIdIndice(arq3.readShort());
              ic.setPosiIndice(arq3.readLong());
              ic.setLapide(arq3.readUTF());
            }

          }

        } else if (contardorPonteiroArq4 != tamCaminho) {// caso 2

          long tamArq2 = arq2.length();
          arq2.seek(tamArq2);

          int qtdElementosarq2 = (int) tamArq4 / 13;
          if (qtdElementosarq2 > tamCaminho) {

            if (qtdElementosarq2 != tamCaminho && (qtdElementosarq2 < (tamCaminho * contadorPRINCIPAL))) {
              qtdElementosarq2 = tamCaminho - (contadorPRINCIPAL - 1);

            } else {
              qtdElementosarq2 = tamCaminho;
            }
          }

          while (contardorPonteiroArq4 < (qtdElementosarq2)) {
            arq2.writeShort(ic2.getIdIndice());
            arq2.writeLong(ic2.getPosiIndice());
            arq2.writeUTF(ic2.getLapide());
            contardorPonteiroArq4++;
            if (contardorPonteiroArq4 < (qtdElementosarq2)) {
              ic2.setIdIndice(arq4.readShort());
              ic2.setPosiIndice(arq4.readLong());
              ic2.setLapide(arq4.readUTF());
            }

          }
        }
      } else {// quando o arq 3 estiver incompleto e logicamente não possuir elementos no arq4

        int qtdElementosNoCaminho = 0;

        qtdElementosNoCaminho = tamanhoArq3Inteiro;
        int novoContador = 0;
        while (novoContador < qtdElementosNoCaminho) {

          ic.setIdIndice(arq3.readShort());
          ic.setPosiIndice(arq3.readLong());
          ic.setLapide(arq3.readUTF());

          arq2.writeShort(ic.getIdIndice());
          arq2.writeLong(ic.getPosiIndice());
          arq2.writeUTF(ic.getLapide());

          novoContador++;
        }
        ultimoSavearq2 = true;
      }

    } catch (Exception e) {
      System.out.println("Deu erro no método ordenar 3 e 4 para 2: " + e.getMessage());
      return false;
    }

    return ultimoSavearq2;

  }

  public static void ordernarToArqIndice(Boolean ultimoSavearq1, boolean ultimoSavearq2, boolean ultimoSavearq3,
      boolean ultimoSavearq4) {
    try {
      RandomAccessFile arqIndice = new RandomAccessFile("src/database/aindices.db", "rw");
      if (ultimoSavearq1 == true) {

        RandomAccessFile arq1 = new RandomAccessFile("src/database/arq1.db", "rw");
        long tamanhoArq1 = arq1.length();
        int tam = (int) tamanhoArq1;
        byte[] ba;
        ba = new byte[tam];
        // copiarTodosOsBytesdoArq(ba, arq1, tam);
        arq1.read(ba);
        arqIndice.seek(0);
        arqIndice.write(ba);
        arq1.close();
      }

      if (ultimoSavearq2 == true) {

        RandomAccessFile arq2 = new RandomAccessFile("src/database/arq2.db", "rw");
        long tamanhoArq2 = arq2.length();
        int tam = (int) tamanhoArq2;
        byte[] ba;
        ba = new byte[tam];
        // copiarTodosOsBytesdoArq(ba, arq1, tam);
        arq2.read(ba);
        arqIndice.seek(0);
        arqIndice.write(ba);
        arq2.close();
      }

      if (ultimoSavearq3 == true) {

        RandomAccessFile arq3 = new RandomAccessFile("src/database/arq3.db", "rw");
        long tamanhoArq3 = arq3.length();
        int tam = (int) tamanhoArq3;
        byte[] ba;
        ba = new byte[tam];
        // copiarTodosOsBytesdoArq(ba, arq1, tam);
        arq3.read(ba);
        arqIndice.seek(0);
        arqIndice.write(ba);
        arq3.close();
      }

      if (ultimoSavearq4 == true) {

        RandomAccessFile arq4 = new RandomAccessFile("src/database/arq4.db", "rw");
        long tamanhoArq4 = arq4.length();
        int tam = (int) tamanhoArq4;
        byte[] ba;
        ba = new byte[tam];
        // copiarTodosOsBytesdoArq(ba, arq1, tam);
        arq4.read(ba);
        arqIndice.seek(0);
        arqIndice.write(ba);
        arq4.close();
      }

      arqIndice.close();
    } catch (Exception e) {
      String error = e.getMessage();
      System.out.println("Erro na finalização da OE (OrdenacaoToIndice): " + error);
    }
  }

  public static void ordenacaoExterna() {
    arquivocrud arqcru = new arquivocrud();
    int tamanhoCaminho = 10;
    int contadorParaSubtrairdoCaminho = 1;
    boolean ultimoSavearq1 = false;
    boolean ultimoSavearq2 = false;
    boolean ultimoSavearq3 = false;
    boolean ultimoSavearq4 = false;

    try {

      RandomAccessFile arq1 = new RandomAccessFile("src/database/arq1.db", "rw");
      RandomAccessFile arq2 = new RandomAccessFile("src/database/arq2.db", "rw");
      RandomAccessFile arq3 = new RandomAccessFile("src/database/arq3.db", "rw");
      RandomAccessFile arq4 = new RandomAccessFile("src/database/arq4.db", "rw");

      // indice ic = new indice();
      // indice ic2 = new indice();
      // pegar o arq com mais caminhos para ler todos
      long tamanhoArq1 = arq1.length();
      tamanhoArq1 = (long) tamanhoArq1 / 13;

      long tamanhoArq2 = arq2.length();

      tamanhoArq2 = (long) tamanhoArq2 / 13;

      long tamanhoArq3 = arq3.length();
      tamanhoArq3 = (long) tamanhoArq3 / 13;

      long tamanhoArq4 = arq4.length();
      tamanhoArq4 = (long) tamanhoArq4 / 13;

      float numeroParaParaExecPRINC = 0;

      if (tamanhoArq1 == tamanhoArq2) {
        numeroParaParaExecPRINC = (float) (tamanhoArq1 + tamanhoArq2) / 10;
      } else {
        if (tamanhoArq1 > tamanhoArq2) {
          numeroParaParaExecPRINC = (float) tamanhoArq1 / 10;

          if (tamanhoArq2 != 0) {
            numeroParaParaExecPRINC += (float) tamanhoArq2 / 10;
          }

        } else {
          numeroParaParaExecPRINC = (float) tamanhoArq2 / 10;
          if (tamanhoArq1 != 0) {
            numeroParaParaExecPRINC += (float) tamanhoArq1 / 10;
          }

        }
      }

      arq1.seek(0);
      arq2.seek(0);
      arq3.seek(0);
      arq4.seek(0);

      int pararExecucaoPrincipal = (int) Math.ceil(numeroParaParaExecPRINC / 2);
      int contadorEXECPrinc = 0;
      int contadorQtdExecCaminhosMsmArq = 0;
      while (contadorEXECPrinc < pararExecucaoPrincipal) {

        if ((contadorEXECPrinc % 2) == 0) {

          arqcru.deletaTudo(-1, -1, -1, 1, 1, -1, -1);

          if (contadorEXECPrinc != 0) {
            tamanhoCaminho *= 2;
            pararExecucaoPrincipal /= 2;
            arq1.seek(0);
            arq2.seek(0);
            arq3.seek(0);
            arq4.seek(0);
            contadorParaSubtrairdoCaminho = 0;
          }

          while (contadorQtdExecCaminhosMsmArq < pararExecucaoPrincipal) {

            if ((contadorQtdExecCaminhosMsmArq % 2) == 0) {// vai ler de 1 e 2 e salvar em 3 e 4

              ultimoSavearq3 = ordernar1e2para3(arq1, arq2, arq3, tamanhoCaminho, contadorParaSubtrairdoCaminho);
              if (ultimoSavearq3) {
                ultimoSavearq1 = false;
                ultimoSavearq2 = false;
                ultimoSavearq4 = false;
              }
            } else {// vai ler de 1 e 2 e salvar em 4

              ultimoSavearq4 = ordernar1e2para4(arq1, arq2, arq4, tamanhoCaminho, contadorParaSubtrairdoCaminho);
              if (ultimoSavearq4) {
                ultimoSavearq1 = false;
                ultimoSavearq2 = false;
                ultimoSavearq3 = false;
              }
            }
            contadorParaSubtrairdoCaminho++;
            contadorQtdExecCaminhosMsmArq++;
          }
        } else {// aqui vai ler arquivo 3 e 4 e salvar em 1 e 2

          arqcru.deletaTudo(-1, 1, 1, -1, -1, -1, -1);
          arq1.seek(0);
          arq2.seek(0);
          tamanhoCaminho *= 2;
          contadorParaSubtrairdoCaminho = 2;

          int pararExecucaoPrincipal2 = (int) Math.floor(numeroParaParaExecPRINC / 2);
          int contador3e4salvarem1e2 = 0;

          while (contador3e4salvarem1e2 < pararExecucaoPrincipal2) {

            if ((contador3e4salvarem1e2 % 2) == 0) {// salvar 3 e 4 no arquivo 1

              ultimoSavearq1 = ordernar3e4para1(arq1, arq3, arq4, tamanhoCaminho, contadorParaSubtrairdoCaminho);
              if (ultimoSavearq1) {
                ultimoSavearq3 = false;
                ultimoSavearq2 = false;
                ultimoSavearq4 = false;
              }

            } else { // vai salvar 3 e 4 no arquivo 2 (FALTA TESTAR ESSE)

              ultimoSavearq2 = ordernar3e4para2(arq2, arq3, arq4, tamanhoCaminho, contadorParaSubtrairdoCaminho);
              if (ultimoSavearq2) {
                ultimoSavearq1 = false;
                ultimoSavearq3 = false;
                ultimoSavearq4 = false;
              }
            }

            contador3e4salvarem1e2++;
            contadorParaSubtrairdoCaminho++;
          }
        }
        contadorEXECPrinc++;
      }
      arq1.close();
      arq2.close();
      arq3.close();
      arq4.close();
      ordernarToArqIndice(ultimoSavearq1, ultimoSavearq2, ultimoSavearq3,
          ultimoSavearq4);
    } catch (Exception e) {
      String error = e.getMessage();
      System.out.println("Erro na finalização da OE: " + error);
    }

  }

  public void ordenacaoDistribuicao() {// Essa funcao está pegando 10 registros em 10 porem salvando no msm
    // arquivo que
    // é o arq 1, tem que intercalar pegou 10 arq 1 + 10 arq 2 + 10 arq2
    corrigirArquivoIndice();// essa funcao tem o objetivo de pegar o arquivo e ver se ele esta com a
    // quantidade de registros pares ou impares e corrigir o embaralhamento do
    // 0 caso seja impar para fazer a ordenacao
    arquivocrud arqcru = new arquivocrud();
    arqcru.deletaTudo(-1, 1, 1, -1, -1, -1, -1);

    try {

      RandomAccessFile arq1 = new RandomAccessFile("src/database/arq1.db", "rw");
      RandomAccessFile arq2 = new RandomAccessFile("src/database/arq2.db", "rw");
      RandomAccessFile arqI = new RandomAccessFile("src/database/aindices.db", "rw");

      long tamArquivoIndice = arqI.length();
      int inteirotamArquivoIndice = (int) tamArquivoIndice;// tamanho total do arquivo

      indice indiceArray[];
      indiceArray = new indice[10];// abri 10 casas de array do objeto indice

      int contadorParaSalvarNoArquivo1 = 0;
      int contadorArrayIndice = 0;
      int contadorPrincipal = 0;
      inteirotamArquivoIndice /= 13;// para pegar a qtd de elementos no arquivo (sem considerar a correcao do 0)
      int inteirotamArquivoIndice2 = inteirotamArquivoIndice;

      inteirotamArquivoIndice2 -= 1;

      if (inteirotamArquivoIndice != 0) {

        while (contadorPrincipal < inteirotamArquivoIndice) {// nao foi retirado o -1 da variavel
          // inteirotamArquivoIndice, pois ela faz o número correto
          // de loops em relacao ao tamanho do arquivo, caso seja
          // necessário um encerramento antes, ele entra no if logo
          // apos o qtdElementosPresentes

          Short idIndiceAD = arqI.readShort();
          indice ic = new indice();
          Long posiIndiceAD = arqI.readLong();
          String lapideAD = arqI.readUTF();
          ic.setIdIndice(idIndiceAD);
          ic.setPosiIndice(posiIndiceAD);
          ic.setLapide(lapideAD);
          indiceArray[contadorArrayIndice] = ic;

          if (contadorParaSalvarNoArquivo1 == 9 || contadorPrincipal == inteirotamArquivoIndice2
              || contadorParaSalvarNoArquivo1 == 19) {// aqui ele testa, se for 9 é que o array indice esta cheio, entao
            // ele ordena e salva, caso for 19 é que o 2 array de indice esta
            // cheio, e se contadorPrincipal = inteirotamArquivo, consiste que
            // chegou no final do arquivo

            int qtdElementosPresente = qtdElementoArrayIndice(indiceArray);
            if ((contadorPrincipal == inteirotamArquivoIndice2) && (contadorParaSalvarNoArquivo1 != 9)
                && (contadorParaSalvarNoArquivo1 != 19)) {// quando o contador for != 9 e != 19 mas igual ao tamanho do
              // arquivo ele ordena e encerra
              inteirotamArquivoIndice = inteirotamArquivoIndice2;
            } // precisa testar com o segundo caminho incompleto e com ele cheio.

            if (qtdElementosPresente != 1) {// ordenacao só quando tiver + de 1 elemento
              ic.quicksortIndice(indiceArray, 0, qtdElementosPresente - 1);
            }

            byte[] retornoByteArray;
            retornoByteArray = ic.toByteArray(indiceArray, qtdElementosPresente);
            indiceArray = new indice[10];
            contadorArrayIndice = -1;

            if (contadorParaSalvarNoArquivo1 >= 0 && contadorParaSalvarNoArquivo1 <= 9) {
              long ultimaPosidoArq1 = arq1.length();
              arq1.seek(ultimaPosidoArq1);
              arq1.write(retornoByteArray);
            }

            if (contadorParaSalvarNoArquivo1 > 9 && contadorParaSalvarNoArquivo1 < 20) {
              long ultimaPosidoArq2 = arq2.length();
              arq2.seek(ultimaPosidoArq2);
              arq2.write(retornoByteArray);
            }

            if (contadorParaSalvarNoArquivo1 == 19) {// quando chegar em 19 ele restaura o contador, pois esse contador
              // que sabe caso o array de indice fique cheio. Quando ele da 2
              // volta completas ele reseta para - 1 pois ja vai fazer um ++
              // antes de ler, então para comecar de 0
              contadorParaSalvarNoArquivo1 = -1;
            }

          }

          contadorParaSalvarNoArquivo1++;
          contadorArrayIndice++;
          contadorPrincipal++;

        }
      }

      ordenacaoExterna();

      arqI.close();
      arq1.close();
      arq2.close();

    } catch (Exception e) {
      String error = e.getMessage();
      System.out.println("Mensagem de Erro: " + error);
      return;
    }

  }

}
