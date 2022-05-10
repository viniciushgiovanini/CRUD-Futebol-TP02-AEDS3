import java.io.*;

public class ordenacaoexterna {

  public static int pegarParteDecimalFloat(float num) {

    String n = String.valueOf(num);
    int numero1 = 0;
    for (int i = 0; i < n.length(); i++) {

      char a = n.charAt(i);

      if (a == '.') {
        i++;

        a = n.charAt(i);
        if (a != '0') {
          numero1 = Character.getNumericValue(a);
        }

        i = n.length();
      }

    }
    return numero1;
  }

  public static int qtdElementoArrayIndice(indice[] a) {
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

  public static boolean corrigirArquivoIndice() {

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
    boolean eImpar = false;
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
          eImpar = true;
        }

      }

      arq.close();

    } catch (Exception e) {
      System.out.println("Aconteceu um error ao corrigir o arquivo de dados: " + e.getMessage());
    }
    return eImpar;
  }

  // FUNCOES DE APOIO ORDENACAO EXTERNA

  public static boolean ordernar1e2para3(RandomAccessFile arq1, RandomAccessFile arq2, RandomAccessFile arq3,
      int tamCaminho) {
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

      short valor1Arq1 = 0;
      short valor2Arq2 = 0;

      if (tamArq1 >= tamCaminho - 1) {
        podeLerArq1 = true;
      }
      if (tamArq2 >= tamCaminho - 1) {
        podeLerArq2 = true;
      }

      if (podeLerArq1 == true) {

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
          while (contardorPonteiroArq1 < tamanhoCaminhoIncompleto1) {
            arq3.writeShort(ic.getIdIndice());
            arq3.writeLong(ic.getPosiIndice());
            arq3.writeUTF(ic.getLapide());
            contardorPonteiroArq1++;
            if (contardorPonteiroArq1 < tamCaminho) {
              ic.setIdIndice(arq1.readShort());
              ic.setPosiIndice(arq1.readLong());
              ic.setLapide(arq1.readUTF());
            }

          }

        } else if (contardorPonteiroArq2 != tamCaminho) {
          int tamanhoCaminhoIncompleto2 = (int) arq2.length();
          long tamArq3 = arq3.length();
          arq3.seek(tamArq3);
          while (contardorPonteiroArq2 < tamanhoCaminhoIncompleto2) {
            arq3.writeShort(ic2.getIdIndice());
            arq3.writeLong(ic2.getPosiIndice());
            arq3.writeUTF(ic2.getLapide());
            contardorPonteiroArq2++;
            if (contardorPonteiroArq2 < tamanhoCaminhoIncompleto2) {
              ic2.setIdIndice(arq2.readShort());
              ic2.setPosiIndice(arq2.readLong());
              ic2.setLapide(arq2.readUTF());
            }

          }
        }
        ultimoSavearq3 = true;
      } else {
        // caso o arquivo 1 tenha dado e o 2 não.
        int tamanhoCaminhoIncompleto1 = (int) arq1.length();
        int novoContador = 0;
        while (novoContador < tamanhoCaminhoIncompleto1) {

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
      int tamCaminho) {

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

      int testarArq1 = tamanhoArq1Inteiro - tamCaminho;
      // int testarArq2 = tamanhoArq2Inteiro - tamCaminho;
      if (testarArq1 >= tamCaminho) {
        podeLerArq1 = true;
      }
      if (tamArq2 > tamCaminho) {
        podeLerArq2 = true;
      }

      if (podeLerArq1 == true && podeLerArq2 == true) {

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
          while (contardorPonteiroArq1 < tamanhoCaminhoIncompleto1) {
            arq4.writeShort(ic.getIdIndice());
            arq4.writeLong(ic.getPosiIndice());
            arq4.writeUTF(ic.getLapide());
            contardorPonteiroArq1++;
            if (contardorPonteiroArq1 < tamanhoCaminhoIncompleto1) {
              ic.setIdIndice(arq1.readShort());
              ic.setPosiIndice(arq1.readLong());
              ic.setLapide(arq1.readUTF());
            }

          }

        } else if (contardorPonteiroArq2 != tamCaminho) {

          long tamArq4 = arq4.length();
          arq4.seek(tamArq4);
          int tamanhoCaminhoIncompleto2 = (int) arq2.length();
          while (contardorPonteiroArq2 <= tamanhoCaminhoIncompleto2) {
            arq4.writeShort(ic2.getIdIndice());
            arq4.writeLong(ic2.getPosiIndice());
            arq4.writeUTF(ic2.getLapide());
            contardorPonteiroArq2++;
            if (contardorPonteiroArq2 < tamanhoCaminhoIncompleto2) {
              ic2.setIdIndice(arq2.readShort());
              ic2.setPosiIndice(arq2.readLong());
              ic2.setLapide(arq2.readUTF());
            }

          }
        }
      } else if (podeLerArq1 == false) {// caso o arquivo 1 seja imcompleto
        int tamanhoCaminhoIncompleto1 = (int) arq1.length();
        int novoCont = 0;
        int qtdElementosNoCaminho = (tamanhoCaminhoIncompleto1 - tamCaminho);
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
      int tamCaminho) {

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
      // int tamanhoArq4Inteiro = (int) tamArq4;

      int testarArq3 = tamanhoArq3Inteiro - tamCaminho;
      // int testarArq2 = tamanhoArq2Inteiro - tamCaminho;
      if (testarArq3 >= tamCaminho) {
        podeLerArq3 = true;
      }
      if (tamArq4 > tamCaminho) {
        podeLerArq4 = true;
      }

      if (podeLerArq3 == true) {

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

          // fazer a comparacao entre valor do arq 3 e do arq 4

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
          while (contardorPonteiroArq3 < tamanhoCaminhoIncompleto3) {
            arq1.writeShort(ic.getIdIndice());
            arq1.writeLong(ic.getPosiIndice());
            arq1.writeUTF(ic.getLapide());
            contardorPonteiroArq3++;
            if (contardorPonteiroArq3 < tamanhoCaminhoIncompleto3) {
              ic.setIdIndice(arq3.readShort());
              ic.setPosiIndice(arq3.readLong());
              ic.setLapide(arq3.readUTF());
            }

          }

        } else if (contardorPonteiroArq4 != tamCaminho) {

          long tamArq1 = arq1.length();
          arq1.seek(tamArq1);
          int tamanhoCaminhoIncompleto4 = (int) arq4.length();
          while (contardorPonteiroArq4 < tamanhoCaminhoIncompleto4) {
            arq1.writeShort(ic2.getIdIndice());
            arq1.writeLong(ic2.getPosiIndice());
            arq1.writeUTF(ic2.getLapide());
            contardorPonteiroArq4++;
            if (contardorPonteiroArq4 < tamanhoCaminhoIncompleto4) {
              ic2.setIdIndice(arq4.readShort());
              ic2.setPosiIndice(arq4.readLong());
              ic2.setLapide(arq4.readUTF());
            }

          }
        }
        ultimoSalveArq1 = true;
      } else {// caso o arquivo 3 esteja incompleto e nao exista registro em arq 4.
        int tamanhoCaminhoIncompleto3 = (int) arq3.length();
        tamanhoArq3Inteiro /= 13;
        int novoContador = 0;
        while (novoContador < tamanhoCaminhoIncompleto3) {

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
      int tamCaminho) {

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

      int testarArq3 = tamanhoArq3Inteiro - tamCaminho;
      // int testarArq2 = tamanhoArq2Inteiro - tamCaminho;
      if (testarArq3 >= tamCaminho) {
        podeLerArq3 = true;
      }
      if (tamArq4 > tamCaminho) {
        podeLerArq4 = true;
      }

      if (podeLerArq3 == true) {

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
            contardorPonteiroArq3++;

          } else {
            arq2.writeShort(ic.getIdIndice());
            arq2.writeLong(ic.getPosiIndice());
            arq2.writeUTF(ic.getLapide());
            podeLerArq3 = true;
            podeLerArq4 = false;
            contardorPonteiroArq4++;

          }
          contadorDeComparacoes++;
        }
        // caso agora o arquivo 4 nao esteja completo e o 3 sim

        if (contardorPonteiroArq3 != tamCaminho) {
          long tamArq2 = arq2.length();
          arq2.seek(tamArq2);
          while (contardorPonteiroArq3 < tamCaminho) {
            arq2.writeShort(ic.getIdIndice());
            arq2.writeLong(ic.getPosiIndice());
            arq2.writeUTF(ic.getLapide());
            contardorPonteiroArq3++;
            if (contardorPonteiroArq3 < tamCaminho) {
              ic.setIdIndice(arq3.readShort());
              ic.setPosiIndice(arq3.readLong());
              ic.setLapide(arq3.readUTF());
            }

          }

        } else if (contardorPonteiroArq4 != tamCaminho) {

          long tamArq2 = arq2.length();
          arq2.seek(tamArq2);
          while (contardorPonteiroArq4 < tamCaminho) {
            arq2.writeShort(ic2.getIdIndice());
            arq2.writeLong(ic2.getPosiIndice());
            arq2.writeUTF(ic2.getLapide());
            contardorPonteiroArq4++;
            if (contardorPonteiroArq4 < tamCaminho) {
              ic2.setIdIndice(arq4.readShort());
              ic2.setPosiIndice(arq4.readLong());
              ic2.setLapide(arq4.readUTF());
            }

          }
        }
      } else {// quando o arq 3 estiver incompleto e logicamente não possuir elementos no arq4

        int tamanhoCaminhoIncompleto3 = (int) arq3.length();
        int novoContador = 0;
        while (novoContador < tamanhoCaminhoIncompleto3) {

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
        numeroParaParaExecPRINC = (float) tamanhoArq1 / 10;
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

      int pararExecucaoPrincipal = (int) Math.ceil(numeroParaParaExecPRINC / 2);
      int contadorEXECPrinc = 0;
      int contadorQtdExecCaminhosMsmArq = 0;
      while (contadorEXECPrinc < pararExecucaoPrincipal) {

        if ((contadorEXECPrinc % 2) == 0) {

          while (contadorQtdExecCaminhosMsmArq < pararExecucaoPrincipal) {

            if ((contadorQtdExecCaminhosMsmArq % 2) == 0) {// vai ler de 1 e 2 e salvar em 3 e 4

              ultimoSavearq3 = ordernar1e2para3(arq1, arq2, arq3, tamanhoCaminho);

            } else {// vai ler de 1 e 2 e salvar em 4

              ultimoSavearq4 = ordernar1e2para4(arq1, arq2, arq4, tamanhoCaminho);

            }
            contadorQtdExecCaminhosMsmArq++;
          }
        } else {// aqui vai ler arquivo 3 e 4 e salvar em 1 e 2

          arqcru.deletaTudo(-1, 1, 1, -1, -1);
          tamanhoCaminho += 10;

          int pararExecucaoPrincipal2 = (int) Math.ceil(numeroParaParaExecPRINC / 2);
          int contador3e4salvarem1e2 = 0;
          while (contador3e4salvarem1e2 < pararExecucaoPrincipal2) {

            if ((contador3e4salvarem1e2 % 2) == 0) {// salvar 3 e 4 no arquivo 1

              ultimoSavearq1 = ordernar3e4para1(arq1, arq3, arq4, tamanhoCaminho);

            } else { // vai salvar 3 e 4 no arquivo 2

              ultimoSavearq2 = ordernar3e4para2(arq2, arq3, arq4, tamanhoCaminho);

            }

            contador3e4salvarem1e2++;
          }
        }
        contadorEXECPrinc++;
      }
      arq1.close();
      arq2.close();
      arq3.close();
      arq4.close();
      ordernarToArqIndice(ultimoSavearq1, ultimoSavearq2, ultimoSavearq3, ultimoSavearq4);
    } catch (Exception e) {
      String error = e.getMessage();
      System.out.println("Erro na finalização da OE: " + error);
    }

  }

  public void ordenacaoDistribuicao() {// Essa funcao está pegando 10 registros em 10 porem salvando no msm
    // arquivo que
    // é o arq 1, tem que intercalar pegou 10 arq 1 + 10 arq 2 + 10 arq2
    boolean eImpar = corrigirArquivoIndice();// essa funcao tem o objetivo de pegar o arquivo e ver se ele esta com a
    // quantidade de registros pares ou impares e corrigir o embaralhamento do
    // 0 caso seja impar para fazer a ordenacao
    arquivocrud arqcru = new arquivocrud();
    arqcru.deletaTudo(-1, 1, 1, -1, -1);

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

      if (eImpar) {// caso a correcao indentifique que os ultimos registros não sao vazios ele
        // retira 1 valor do tamanho do arquivo, o valor adicionado pelo contador, caso
        // seja impar ele retira o valor referente ao contador + a correcao de 0
        inteirotamArquivoIndice2 -= 2;
      } else {
        inteirotamArquivoIndice2 -= 1;
      }

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
