
import java.util.Scanner;
import java.io.RandomAccessFile;
import java.io.PrintWriter;

public class arquivocrud {

  // --------------------------------------
  // Método deletaTudo é um método que apaga todo o arquivo !
  // --------------------------------------
  public void deletaTudo(int valor, int valor1, int valor2, int valor3, int valor4) {

    try {

      if (valor == 1) {
        PrintWriter writer = new PrintWriter("src/database/futebol.db");
        PrintWriter writer2 = new PrintWriter("src/database/aindices.db");
        writer.print("");
        writer.close();
        writer2.print("");
        writer2.close();

      }

      if (valor1 == 1) {
        PrintWriter writer3 = new PrintWriter("src/database/arq1.db");
        writer3.print("");
        writer3.close();

      }

      if (valor2 == 1) {
        PrintWriter writer4 = new PrintWriter("src/database/arq2.db");
        writer4.print("");
        writer4.close();
      }

      if (valor3 == 1) {
        PrintWriter writer5 = new PrintWriter("src/database/arq3.db");
        writer5.print("");
        writer5.close();

      }

      if (valor4 == 1) {
        PrintWriter writer6 = new PrintWriter("src/database/arq4.db");
        writer6.print("");
        writer6.close();
      }

    } catch (Exception e) {
      System.out.println("ERRO NO DELETA TUDO");
    }

  }

  // --------------------------------------
  // Método escreverArquivo, esse método recebe o objeto ft com os dados ja
  // atribuidos a cada atributo, ele vai pegar qual vai ser o ID atribuido a esse
  // objeto e onde ele vai ser adicionado no arquivo.
  // --------------------------------------

  public void escreverArquivo(fut ft) {

    /*
     * como ta sendo feita a escrita
     * ID COMECO DO ARQUIVO + Tam do Arquiv +
     * ARRAYDEBYTE(ID+LAPIDE+NOME+CNPJ+CIDADE+PARTIDASJOGADAS+PONTOS)
     */
    // Escrita no Arquivo
    RandomAccessFile arq;
    byte[] ba;
    long posiIndice = 0;

    try {
      // verificarArquivo("dados/futebol.db");
      short idcabecalhosave = 0;
      indice ic = new indice();
      arq = new RandomAccessFile("src/database/futebol.db", "rw");

      if (arq.length() == 0) {
        idcabecalhosave = ft.getIdClube();
        arq.writeShort(idcabecalhosave);

      }
      arq.seek(0);
      idcabecalhosave = arq.readShort();
      idcabecalhosave++;
      arq.seek(0);
      arq.writeShort(idcabecalhosave);

      // System.out.println(arq.getFilePointer());
      long finaldoarquivo = (long) arq.length();
      arq.seek(finaldoarquivo);
      posiIndice = finaldoarquivo;
      // System.out.println(arq.getFilePointer());

      ft.setIdClube(idcabecalhosave);
      ba = ft.toByteArray();
      arq.writeInt(ba.length);
      arq.write(ba);

      // local que faz a escrita no arquivo
      ic.setIdIndice(idcabecalhosave);
      ic.setPosiIndice(posiIndice);
      ic.writeIndicetoArq();

    } catch (Exception e) {
      String erro = e.getMessage();

      if (erro.contains("No such file or directory")) {

        System.out.println("Diretório do arquivo não encontrado !");
        return;
      }

    }

    System.out.println("------X------");
    System.out.println(ft.toString());

  }

  // --------------------------------------
  // Método criarClube, esse método tem como objeto pegar os dados de um novo
  // clube e atribuir ao objeto fut, e chamar escreverArquivo.
  // --------------------------------------

  public void criarClube(Scanner entrada) {

    fut ft = new fut();

    String cnpjparaveri = null;

    System.out.print("Escreva o nome do clube: ");
    ft.setNome(entrada.nextLine());

    if (!(ft.getNome().equals(""))) {

      System.out.println();
      System.out.print("Insira o cnpj do clube: ");
      cnpjparaveri = entrada.nextLine();// AQUI PRECISA TRATAR O CPNJ;
      ft.setCnpj(cnpjparaveri);
      System.out.println();
      System.out.print("Insira a cidade do clube: ");
      ft.setCidade(entrada.nextLine());

      escreverArquivo(ft);
    } else {
      System.out.println("\nArquivo com o Campo nome vazio não é possivel ser escrito !\n");
      return;
    }

  }

  // -------------------Create - FIM---------------------------------//

  // ----------------------READ-------------------------//

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

  public void ordenacaoExterna() {
    int tamanhoCaminho = 10;
    long contador1 = 0;
    int contador2 = 0;
    int contador3 = 0;
    boolean ultimoSavearq1 = false;
    boolean ultimoSavearq2 = false;
    boolean ultimoSavearq3 = false;
    boolean ultimoSavearq4 = false;

    try {

      RandomAccessFile arq1 = new RandomAccessFile("src/database/arq1.db", "rw");
      RandomAccessFile arq2 = new RandomAccessFile("src/database/arq2.db", "rw");
      RandomAccessFile arq3 = new RandomAccessFile("src/database/arq3.db", "rw");
      RandomAccessFile arq4 = new RandomAccessFile("src/database/arq4.db", "rw");
      indice ic = new indice();
      indice ic2 = new indice();
      // pegar o arq com mais caminhos para ler todos
      long tamanhoArq1 = arq1.length();
      tamanhoArq1 = (long) Math.ceil(tamanhoArq1 / 13);

      long tamanhoArq2 = arq2.length();

      tamanhoArq2 = (long) Math.ceil(tamanhoArq2 / 13);

      long tamanhoPararExecucao = 0;

      if (tamanhoArq1 == tamanhoArq2) {
        tamanhoPararExecucao = (long) Math.ceil(tamanhoArq1 / 10);
      } else {
        if (tamanhoArq1 > tamanhoArq2) {
          tamanhoPararExecucao = (long) Math.ceil(tamanhoArq1 / 10);
        } else {
          tamanhoPararExecucao = (long) Math.ceil(tamanhoArq2 / 10);
        }
      }
      int qtdExecucaoPrincipal = (int) Math.ceil(tamanhoPararExecucao / 2) + 1;
      long valor1 = 0;
      long valor2 = 0;
      int contadorPontarq1 = 0;
      int contadorPontarq2 = 0;
      boolean podeLerV1 = true;
      boolean podeLerV2 = true;

      while (contador3 < qtdExecucaoPrincipal) {// laco que muda os 2 arquivores leitores (1 e 2) para o arquivo
                                                // salvador (3 e 4)

        if ((contador3 % 2) == 0) {// vai ler de 1 e 2 e salvar em 3 e 4
          deletaTudo(-1, -1, -1, 1, 1);
          if (contador3 != 0) {
            tamanhoCaminho += 10;
            tamanhoPararExecucao = (int) Math.round(tamanhoPararExecucao / 2);
            arq1.seek(0);
            arq2.seek(0);
            arq3.seek(0);
            arq4.seek(0);
          }
          while (contador1 < tamanhoPararExecucao) {// laco que muda entre os arquivos salvadores

            contadorPontarq1 = 0;
            contadorPontarq2 = 0;
            contador2 = 0;

            if ((contador1 % 2) == 0) {// vai ler de 1 e 2 e salvar em 3

              while (contador2 < tamanhoCaminho) {

                if (podeLerV1) {
                  // leu o registro do 1 arquivo
                  ic.setIdIndice(arq1.readShort());
                  ic.setPosiIndice(arq1.readLong());
                  ic.setLapide(arq1.readUTF());
                  valor1 = ic.getIdIndice();

                }

                if (podeLerV2) {
                  // leu o registro do 2 arquivo

                  ic2.setIdIndice(arq2.readShort());
                  valor2 = ic2.getIdIndice();
                  ic2.setPosiIndice(arq2.readLong());
                  ic2.setLapide(arq2.readUTF());

                }

                // fazer a comparacao

                if (valor2 < valor1) {

                  arq3.writeShort(ic2.getIdIndice());
                  arq3.writeLong(ic2.getPosiIndice());
                  arq3.writeUTF(ic2.getLapide());
                  podeLerV1 = false;
                  podeLerV2 = true;
                  contadorPontarq2++;

                } else {
                  arq3.writeShort(ic.getIdIndice());
                  arq3.writeLong(ic.getPosiIndice());
                  arq3.writeUTF(ic.getLapide());
                  podeLerV1 = true;
                  podeLerV2 = false;
                  contadorPontarq1++;

                }

                contador2++;
              }

              if (contadorPontarq1 != tamanhoCaminho) {
                long tamArq3 = arq3.length();
                arq3.seek(tamArq3);
                while (contadorPontarq1 <= tamanhoCaminho - 1) {
                  arq3.writeShort(ic.getIdIndice());
                  arq3.writeLong(ic.getPosiIndice());
                  arq3.writeUTF(ic.getLapide());
                  contadorPontarq1++;
                  if (contadorPontarq1 < tamanhoCaminho) {
                    ic.setIdIndice(arq1.readShort());
                    ic.setPosiIndice(arq1.readLong());
                    ic.setLapide(arq1.readUTF());
                  }

                }

              } else if (contadorPontarq2 != tamanhoCaminho) {

                long tamArq3 = arq3.length();
                arq3.seek(tamArq3);
                while (contadorPontarq2 <= tamanhoCaminho - 1) {
                  arq3.writeShort(ic2.getIdIndice());
                  arq3.writeLong(ic2.getPosiIndice());
                  arq3.writeUTF(ic2.getLapide());
                  contadorPontarq2++;
                  if (contadorPontarq2 < tamanhoCaminho) {
                    ic2.setIdIndice(arq2.readShort());
                    ic2.setPosiIndice(arq2.readLong());
                    ic2.setLapide(arq2.readUTF());
                  }

                }
              }
              contador1++;
            } else {// vai ler de 1 e 2 e salvar em 4
              contador2 = 0;
              contadorPontarq1 = 0;
              contadorPontarq2 = 0;
              podeLerV1 = true;
              podeLerV2 = true;
              valor1 = 0;
              valor2 = 0;
              while (contador2 < tamanhoCaminho) {

                if (podeLerV1) {
                  // leu o registro do 1 arquivo
                  ic.setIdIndice(arq1.readShort());
                  ic.setPosiIndice(arq1.readLong());
                  ic.setLapide(arq1.readUTF());
                  valor1 = ic.getIdIndice();

                }

                if (podeLerV2) {
                  // leu o registro do 2 arquivo

                  ic2.setIdIndice(arq2.readShort());
                  valor2 = ic2.getIdIndice();
                  ic2.setPosiIndice(arq2.readLong());
                  ic2.setLapide(arq2.readUTF());

                }

                // fazer a comparacao

                if (valor2 < valor1) {

                  arq4.writeShort(ic2.getIdIndice());
                  arq4.writeLong(ic2.getPosiIndice());
                  arq4.writeUTF(ic2.getLapide());
                  podeLerV1 = false;
                  podeLerV2 = true;
                  contadorPontarq2++;

                } else {
                  arq4.writeShort(ic.getIdIndice());
                  arq4.writeLong(ic.getPosiIndice());
                  arq4.writeUTF(ic.getLapide());
                  podeLerV1 = true;
                  podeLerV2 = false;
                  contadorPontarq1++;

                }

                contador2++;
              }

              if (contadorPontarq1 != tamanhoCaminho) {
                long tamArq4 = arq4.length();
                arq4.seek(tamArq4);
                while (contadorPontarq1 <= tamanhoCaminho - 1) {
                  arq4.writeShort(ic.getIdIndice());
                  arq4.writeLong(ic.getPosiIndice());
                  arq4.writeUTF(ic.getLapide());
                  contadorPontarq1++;
                  if (contadorPontarq1 < tamanhoCaminho) {
                    ic.setIdIndice(arq1.readShort());
                    ic.setPosiIndice(arq1.readLong());
                    ic.setLapide(arq1.readUTF());
                  }

                }

              } else if (contadorPontarq2 != tamanhoCaminho) {

                long tamArq4 = arq4.length();
                arq4.seek(tamArq4);
                while (contadorPontarq2 <= tamanhoCaminho - 1) {
                  arq4.writeShort(ic2.getIdIndice());
                  arq4.writeLong(ic2.getPosiIndice());
                  arq4.writeUTF(ic2.getLapide());
                  contadorPontarq2++;
                  if (contadorPontarq2 < tamanhoCaminho) {
                    ic2.setIdIndice(arq2.readShort());
                    ic2.setPosiIndice(arq2.readLong());
                    ic2.setLapide(arq2.readUTF());
                  }

                }
              }
              contador1++;
            }
          }

        } else {// ler arquivo 3 e 4 e salvar em 1 e 2

          deletaTudo(-1, 1, 1, -1, -1);

          tamanhoCaminho += 10;
          podeLerV1 = true;
          podeLerV2 = true;
          int contadorPontarq3 = 0;
          int contadorPontarq4 = 0;
          valor1 = 0;
          valor2 = 0;
          contador1 = 0;
          tamanhoPararExecucao = (int) Math.round(tamanhoPararExecucao / 2);
          while (contador1 < tamanhoPararExecucao) {// laco que muda os 2 arquivores leitores (3 e 4) para o
                                                    // arquivo
            // salvador (1 e 2)
            arq1.seek(0);
            arq2.seek(0);
            arq3.seek(0);
            arq4.seek(0);
            contadorPontarq3 = 0;
            contadorPontarq4 = 0;
            contador2 = 0;

            if ((contador1 % 2) == 0) {// vai ler de 3 e 4 e salvar em 1 e 2

              while (contador2 < tamanhoCaminho) {// laco que salva do arquivo 3 e 4 no arquivo 3

                if (podeLerV1) {
                  // leu o registro do 1 arquivo
                  ic.setIdIndice(arq3.readShort());
                  ic.setPosiIndice(arq3.readLong());
                  ic.setLapide(arq3.readUTF());
                  valor1 = ic.getIdIndice();

                }

                if (podeLerV2) {
                  // leu o registro do 2 arquivo

                  ic2.setIdIndice(arq4.readShort());
                  valor2 = ic2.getIdIndice();
                  ic2.setPosiIndice(arq4.readLong());
                  ic2.setLapide(arq4.readUTF());

                }

                // fazer a comparacao

                if (valor2 < valor1) {

                  arq1.writeShort(ic2.getIdIndice());
                  arq1.writeLong(ic2.getPosiIndice());
                  arq1.writeUTF(ic2.getLapide());
                  podeLerV1 = false;
                  podeLerV2 = true;
                  contadorPontarq4++;

                } else {
                  arq1.writeShort(ic.getIdIndice());
                  arq1.writeLong(ic.getPosiIndice());
                  arq1.writeUTF(ic.getLapide());
                  podeLerV1 = true;
                  podeLerV2 = false;
                  contadorPontarq3++;

                }

                contador2++;
              }

              if (contadorPontarq3 != tamanhoCaminho) {
                long tamArq1 = arq1.length();
                arq1.seek(tamArq1);
                while (contadorPontarq3 <= tamanhoCaminho - 1) {
                  arq1.writeShort(ic.getIdIndice());
                  arq1.writeLong(ic.getPosiIndice());
                  arq1.writeUTF(ic.getLapide());
                  contadorPontarq3++;
                  if (contadorPontarq3 < tamanhoCaminho) {
                    ic.setIdIndice(arq3.readShort());
                    ic.setPosiIndice(arq3.readLong());
                    ic.setLapide(arq3.readUTF());
                  }

                }

              } else if (contadorPontarq4 != tamanhoCaminho) {

                long tamArq1 = arq1.length();
                arq1.seek(tamArq1);
                while (contadorPontarq4 <= tamanhoCaminho - 1) {
                  arq1.writeShort(ic2.getIdIndice());
                  arq1.writeLong(ic2.getPosiIndice());
                  arq1.writeUTF(ic2.getLapide());
                  contadorPontarq4++;
                  if (contadorPontarq4 < tamanhoCaminho) {
                    ic2.setIdIndice(arq4.readShort());
                    ic2.setPosiIndice(arq4.readLong());
                    ic2.setLapide(arq4.readUTF());
                  }

                }
              }
              contador1++;
            } else {// vai ler de 3 e 4 e salvar em 4
              // pegar codigo acima e ler do arquivo 3 e 4 e salvar no 4.

              contadorPontarq3 = 0;
              contadorPontarq4 = 0;
              podeLerV1 = true;
              podeLerV2 = true;
              valor1 = 0;
              valor2 = 0;

              while (contador2 < tamanhoCaminho) {// laco que salva do arquivo 3 e 4 no arquivo 4

                if (podeLerV1) {
                  // leu o registro do 1 arquivo
                  ic.setIdIndice(arq3.readShort());
                  ic.setPosiIndice(arq3.readLong());
                  ic.setLapide(arq3.readUTF());
                  valor1 = ic.getIdIndice();

                }

                if (podeLerV2) {
                  // leu o registro do 2 arquivo

                  ic2.setIdIndice(arq4.readShort());
                  valor2 = ic2.getIdIndice();
                  ic2.setPosiIndice(arq4.readLong());
                  ic2.setLapide(arq4.readUTF());

                }

                // fazer a comparacao

                if (valor2 < valor1) {

                  arq2.writeShort(ic2.getIdIndice());
                  arq2.writeLong(ic2.getPosiIndice());
                  arq2.writeUTF(ic2.getLapide());
                  podeLerV1 = false;
                  podeLerV2 = true;
                  contadorPontarq4++;

                } else {
                  arq2.writeShort(ic.getIdIndice());
                  arq2.writeLong(ic.getPosiIndice());
                  arq2.writeUTF(ic.getLapide());
                  podeLerV1 = true;
                  podeLerV2 = false;
                  contadorPontarq3++;

                }

                contador2++;
              }

              if (contadorPontarq3 != tamanhoCaminho) {
                long tamArq2 = arq2.length();
                arq2.seek(tamArq2);
                while (contadorPontarq3 <= tamanhoCaminho - 1) {
                  arq2.writeShort(ic.getIdIndice());
                  arq2.writeLong(ic.getPosiIndice());
                  arq2.writeUTF(ic.getLapide());
                  contadorPontarq3++;
                  if (contadorPontarq3 < tamanhoCaminho) {
                    ic.setIdIndice(arq3.readShort());
                    ic.setPosiIndice(arq3.readLong());
                    ic.setLapide(arq3.readUTF());
                  }

                }

              } else if (contadorPontarq4 != tamanhoCaminho) {

                long tamArq2 = arq2.length();
                arq2.seek(tamArq2);
                while (contadorPontarq4 <= tamanhoCaminho - 1) {
                  arq2.writeShort(ic2.getIdIndice());
                  arq2.writeLong(ic2.getPosiIndice());
                  arq2.writeUTF(ic2.getLapide());
                  contadorPontarq4++;
                  if (contadorPontarq4 < tamanhoCaminho) {
                    ic2.setIdIndice(arq4.readShort());
                    ic2.setPosiIndice(arq4.readLong());
                    ic2.setLapide(arq4.readUTF());
                  }

                }
              }
              contador1++;
            }

          }

        }

        contador3++;
      }
    } catch (Exception e) {
      String error = e.getMessage();
      System.out.println("Erro: " + error);
    }

  }

  public void ordenacaoDistribuicao() {// Essa funcao está pegando 10 registros em 10 porem salvando no msm
                                       // arquivo que
    // é o arq 1, tem que intercalar pegou 10 arq 1 + 10 arq 2 + 10 arq2
    boolean eImpar = corrigirArquivoIndice();// essa funcao tem o objetivo de pegar o arquivo e ver se ele esta com a
                                             // quantidade de registros pares ou impares e corrigir o embaralhamento do
                                             // 0 caso seja impar para fazer a ordenacao

    deletaTudo(-1, 1, 1, -1, -1);

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

  // --------------------------------------
  // Método pesquisaBinariaArquivoIndice faz a busca binaria no arquivo de
  // indices, dessa forma retornando a posicao long no arquivo de dados, e logo
  // ja testa se o arquivo está deletado ou não. OBS esse método só faz a procura
  // de números.
  // --------------------------------------
  public long pesquisaBinariaArquivoIndice(int n) {
    long posicaoRetorno = -1;

    try {
      RandomAccessFile arq = new RandomAccessFile("src/database/aindices.db", "r");

      if (arq.length() != 0) {

        arq.seek(0);
        int esq = arq.readShort();
        int qtdElementos = (int) arq.length() / 10;
        arq.seek(arq.length() - 10);
        int dir = arq.readShort();
        int mid = (esq + dir) / 2;
        arq.seek(0);

        while (esq <= dir) {
          mid = (esq + dir) / 2;
          arq.seek(mid * 10);// Para chegar no numero desejado é so pegar o numero e X 10, pois cada 1
                             // registro nesse arquivo de incide ocupa 10 bytes 2short e 8 do long,
                             // respectivo ao ID e ao Endereço dele.
          int numerodoMeio = arq.readShort();
          if (n == numerodoMeio) {
            posicaoRetorno = arq.readLong();
            esq = qtdElementos;
          } else if (n > numerodoMeio) {

            esq = mid + 1;
          } else {
            dir = mid - 1;
          }
        }
      } else {
        System.out.println("ERROR: O arquivo de busca se encontra vazio !");
        posicaoRetorno = -10;
      }

      arq.close();

    } catch (Exception e) {
      String erro = e.getMessage();

      if (erro.contains("No such file or directory")) {

        System.out.println("(PB) Diretório do arquivo não encontrado ! ERROR: " + e.getMessage());
        return -10;
      } else {
        System.out.println("ERROR: " + erro);
        return -10;
      }
    }

    return posicaoRetorno;

  }

  // --------------------------------------
  // COMENTA NOVAMENTE PROCURAR CLUBE POIS FOI FUNDIDO COM O METODO
  // pesquisarNoArquivo
  // --------------------------------------

  public long procurarClube(String recebendo, fut ft2) {

    /*
     * como ta sendo feita a escrita
     * ID COMECO DO ARQUIVO + Tam do Arquiv +
     * ARRAYDEBYTE(ID+LAPIDE+NOME+CNPJ+CIDADE+PARTIDASJOGADAS+PONTOS)
     */
    // Escrita no Arquivo
    long retornoPesquisa = -1;
    boolean idOrnot = recebendo.matches("-?\\d+");
    String lapide = "*";

    if (idOrnot == true) {// Inicio Pesquisa Númerica
      ordenacaoDistribuicao();
      int entradaInt = Integer.parseInt(recebendo);
      retornoPesquisa = pesquisaBinariaArquivoIndice(entradaInt);// chama a pesquisa binária

      byte[] ba;
      RandomAccessFile arq;

      if (retornoPesquisa >= 0) {

        try {
          arq = new RandomAccessFile("src/database/futebol.db", "rw");
          arq.seek(retornoPesquisa + 6);
          String lapideLida = arq.readUTF();

          if (!(lapideLida.equals(lapide))) {

            arq.seek(retornoPesquisa);
            int tamRegistro = arq.readInt();
            ba = new byte[tamRegistro];
            arq.read(ba);
            ft2.fromByteArray(ba);
          } else {
            retornoPesquisa = -1;
          }

        } catch (Exception e) {
          String erro = e.getMessage();

          if (erro.contains("No such file or directory")) {

            System.out.println("\nDiretório do arquivo não encontrado ! ERROR: " + e.getMessage());
            return -10;
          } else {
            System.out.println("ERROR: " + e.getMessage());
            return -10;
          }
        }
      } else {
        if (retornoPesquisa == -1) {

          System.out.println("\nRegistro Pesquisado não encontrado !\n");

        }
      }
    } // aqui provavelmente vai ter que ser implementado a lista invertida.
    return retornoPesquisa;
  }

  // ----------------------READ - FIM-------------------------//

  // --------------------------------------
  // Método arquivoDelete, tem como parametro a string ID, que é o nome ou ID do
  // resgistro, e o objeto criado no arquivo main para ser atribuido, ele pesquisa
  // se o ID a ser deletado existe, ele imprime o registro e pede uma confirmacao
  // para prosseguir com o delete, se for positivo essa verificação ele marca a
  // lapide.
  // --------------------------------------

  // ----------------------Delete-------------------------//
  public void arquivoDelete(String id, Scanner verificarultimoDelete, fut ft2) {

    RandomAccessFile arq;
    String lapide = "";
    boolean arquivoDeletado = false;
    try {
      arq = new RandomAccessFile("src/database/futebol.db", "rw");

      long idExist = procurarClube(id, ft2);

      if (idExist >= 0) {

        System.out.println(ft2.toString());

        System.out.println("Você deseja deletar esse registro ?");
        String ultVeri = verificarultimoDelete.nextLine();

        if ((ultVeri.toLowerCase().equals("sim") == true)) {
          arq.seek(idExist + 6);
          lapide = "*";
          // System.out.println(arq.getFilePointer());
          arq.writeUTF(lapide);
          arquivoDeletado = true;
        } else {
          System.out.println("Registro não Deletado");
        }

      }

      else {
        System.out.println("Registro não Deletado !");
      }

    } catch (Exception e) {
      System.out.println("Erro quando foi deletar um registro. ERROR: " + e.getMessage());
    }

    if (arquivoDeletado == true) {

      System.out.println("Registro Deletado com Sucesso");

    }

  }

  // ----------------------Delete - FINAL-------------------------//

  // -----------------------UPDATE---------------------------------//
  // --------------------------------------
  // Método Update, recebe como parametro, uma string que é o ID e o Nome a ser
  // atulizado, o scanner, o tipo de Update, e caso for parcial, ele recebe os
  // pontos para ser atualizado. Essa funcao pode fazer um update completo
  // alterando todos os atributos do registro e um parcial que é usado no método
  // realizar partida, na qual altera somente os pts com o que for passado e o
  // partidasJogadas adicionando + 1, retornando true caso o update seja feito com
  // sucesso e false caso de erro no update
  // --------------------------------------
  public boolean arquivoUpdate(String nomeidProcurado, Scanner entradaUpdate, String tipoDeUpdate, byte Pts,
      fut futebasParcial) {

    /*
     * como ta sendo feita a escrita
     * ID COMECO DO ARQUIVO + Tam do Arquiv +
     * ARRAYDEBYTE(ID+LAPIDE+NOME+CNPJ+CIDADE+PARTIDASJOGADAS+PONTOS)
     */

    RandomAccessFile arq;

    if (tipoDeUpdate.equals("Completo")) {
      fut ft2 = new fut();
      long receberProcura = procurarClube(nomeidProcurado, ft2);
      byte[] ba;
      String stgConfirma = "";

      if (receberProcura >= 0) {

        System.out.println("Você deseja Atualizar o Registro abaixo ?");
        System.out.println(ft2.toString());
        System.out.print("Inserir Resposta: ");
        stgConfirma = entradaUpdate.nextLine();

        if (stgConfirma.toUpperCase().equals("SIM")) {

          try {
            arq = new RandomAccessFile("src/database/futebol.db", "rw");
            arq.seek(receberProcura);
            int tamanhoArquivoVelho = arq.readInt();

            System.out.print("Atualize o nome do Clube: ");
            ft2.setNome(entradaUpdate.nextLine());
            System.out.println();
            System.out.print("Atualize o CNPJ do Clube: ");
            ft2.setCnpj(entradaUpdate.nextLine());
            System.out.println();
            System.out.print("Atualize a Cidade do Clube: ");
            ft2.setCidade(entradaUpdate.nextLine());
            System.out.println();
            System.out.print("Atualize as Partidas Jogadas do Clube: ");
            ft2.setPartidasJogadas(entradaUpdate.nextByte());
            System.out.println();
            System.out.print("Atualize os Pontos do Clube: ");
            ft2.setPontos(entradaUpdate.nextByte());

            ba = ft2.toByteArray();
            int tamanhoArquivoNovo = ba.length;

            if (tamanhoArquivoNovo <= tamanhoArquivoVelho) {

              ba = ft2.toByteArray();
              arq.seek(receberProcura + 4);
              arq.write(ba);
              System.out.println("Arquivo Escrito com Sucesso !");

            } else {
              arq.seek(0);
              // peganto tam total do arq
              long tamanhoTotalArq = arq.length();
              // pegando Id do cabecalho
              arq.seek(0);
              Short pegarPrimeiroId = 0;
              pegarPrimeiroId = arq.readShort();
              // marcando lapide
              arq.seek(0);
              arq.seek(receberProcura + 6);
              // System.out.println(arq.getFilePointer());
              String lapide = "*";
              arq.writeUTF(lapide);

              // indo para o final do arquivo
              arq.seek(0);
              arq.seek(tamanhoTotalArq);
              pegarPrimeiroId++;
              ft2.setIdClube(pegarPrimeiroId);

              ba = ft2.toByteArray();
              arq.writeInt(ba.length);
              arq.write(ba);

              arq.seek(0);
              arq.writeShort(pegarPrimeiroId);

              System.out.println("Arquivo Atualizado com Sucesso !");
            }

          } catch (Exception e) {
            System.out.println("Aconteceu um ERROR: " + e.getMessage());
            return false;
          }

        } else {
          System.out.println("Arquivo NÃO atualizado !!!");
          return false;
        }
      } else {
        System.out.println("Arquivo NÃO atualizado !!!");
        return false;
      }
    } else {
      if (tipoDeUpdate.equals("Parcial")) {

        long receberProcura = procurarClube(nomeidProcurado, futebasParcial);
        byte[] ba;

        if (receberProcura >= 0) {

          try {
            arq = new RandomAccessFile("src/database/futebol.db", "rw");
            arq.seek(receberProcura);
            int tamanhoArquivoVelho = arq.readInt();

            byte numParti = futebasParcial.getPartidasJogadas();

            if (numParti <= 40) {
              futebasParcial.setPartidasJogadas(++numParti);
            } else {
              System.out.println("Numero Maximo de confrontos atingidos (20)");
              arq.close();
              return false;
            }

            byte qtdPonto = futebasParcial.getPontos();
            qtdPonto += Pts;
            if (qtdPonto <= 125) {
              futebasParcial.setPontos(qtdPonto);
            } else {
              System.out.println("Clube alcançou a quantide maxima de pontos de um Campeonato (125)");
              arq.close();
              return false;
            }

            ba = futebasParcial.toByteArray();
            int tamanhoArquivoNovo = ba.length;

            if (tamanhoArquivoNovo <= tamanhoArquivoVelho) {

              ba = futebasParcial.toByteArray();
              arq.seek(receberProcura + 4);
              arq.write(ba);
              System.out.println("Arquivo Atulizado com Sucesso !\n");

            } else {
              arq.seek(0);
              // peganto tam total do arq
              long tamanhoTotalArq = arq.length();
              // pegando Id do cabecalho
              arq.seek(0);
              Short pegarPrimeiroId = 0;
              pegarPrimeiroId = arq.readShort();
              // marcando lapide
              arq.seek(0);
              arq.seek(receberProcura + 6);
              // System.out.println(arq.getFilePointer());
              String lapide = "*";
              arq.writeUTF(lapide);

              // indo para o final do arquivo
              arq.seek(0);
              arq.seek(tamanhoTotalArq);
              pegarPrimeiroId++;
              futebasParcial.setIdClube(pegarPrimeiroId);

              ba = futebasParcial.toByteArray();
              arq.writeInt(ba.length);
              arq.write(ba);

              arq.seek(0);
              arq.writeShort(pegarPrimeiroId);

            }
            arq.close();
          } catch (Exception e) {
            System.out.println("Aconteceu um ERROR: " + e.getMessage());
            return false;
          }

        } else {
          System.out.println("Arquivo NÃO atualizado !!!");
          return false;
        }
      } else {
        System.out.println("Arquivo NÃO atualizado !!!");
        return false;
      }
    }

    return true;
  }
  // -----------------------UPDATE - FINAL---------------------------------//

}
