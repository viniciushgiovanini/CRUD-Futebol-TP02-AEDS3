import java.util.Scanner;
import java.io.RandomAccessFile;

import java.io.PrintWriter;

public class arquivocrud {

  // -------------------FUNCAO-PARA-TESTAR-O-ARQUIVO----------------//
  // --------------------------------------
  // Método deletaTudo é um método que apaga todo o arquivo !
  // --------------------------------------
  public void deletaTudo() {

    try {

      PrintWriter writer = new PrintWriter("src/database/futebol.db");
      writer.print("");
      writer.close();

    } catch (Exception e) {
      System.out.println("ERRO NO DELETA TUDO");
    }

  }

  // -------------------Create---------------------------------//

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

    try {
      // verificarArquivo("dados/futebol.db");
      short idcabecalhosave = 0;
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
      // System.out.println(arq.getFilePointer());

      ft.setIdClube(idcabecalhosave);
      ba = ft.toByteArray();
      arq.writeInt(ba.length);
      arq.write(ba);

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

  // --------------------------------------
  // Método pesquisarNoArquivo recebe uma String a ser pesquisada que pode ser o
  // ID ou o Nome presente no registro, caso for ID ele pesquisa pelo ID, caso for
  // Nome, ele pesquisa pelo nome de cada registro, retorna a posicao do arquivo
  // caso encontrar, retorna -1 caso não encontre o arquivo, retorna -10 em caso
  // de erro na pesquisa
  // --------------------------------------

  public long pesquisarNoArquivo(String entrada) {

    RandomAccessFile arq;
    String lapide = "";
    long posicaoRetorno = -1;
    boolean idOrnot = entrada.matches("-?\\d+");
    boolean idDeletado = false;
    boolean idencontrado = false;
    boolean idnexiste = false;
    long testeArquivoVazio = 0;

    if (idOrnot == true) {
      long posicaosave = 0;
      try {

        arq = new RandomAccessFile("src/database/futebol.db", "rw");
        testeArquivoVazio = arq.length();

        if (testeArquivoVazio != 0) {

          short idproc = Short.valueOf(entrada);
          arq.seek(2);
          posicaoRetorno = arq.getFilePointer();
          int tam = arq.readInt();
          posicaosave = arq.getFilePointer();
          short idlido = arq.readShort();
          int contador = 0;

          long ultimaPosiArq = (long) arq.length();

          while (contador <= idproc && idencontrado == false && idnexiste == false) {

            if (idlido == idproc) {

              lapide = arq.readUTF();
              idencontrado = true;
              if ((lapide.equals(" ") == true)) {
                idDeletado = false;
              } else {
                idDeletado = true;
              }

            }

            if ((idencontrado == false) && (posicaosave + tam < ultimaPosiArq) && (contador <= idproc)) {
              arq.seek(posicaosave);
              int converlt = (int) posicaosave;
              posicaosave = (long) tam + converlt;
              arq.seek(posicaosave);

              posicaoRetorno = arq.getFilePointer();
              tam = arq.readInt();
              posicaosave = arq.getFilePointer();
              idlido = arq.readShort();

            } else {

              if (idencontrado == false) {
                idnexiste = true;
              }

            }

            contador++;

          }
        } else {
          System.out.println("O Arquivo está Vazio, nada para ser Procurado !");
        }
        arq.close();
      } catch (Exception e) {
        String erro = e.getMessage();

        if (erro.contains("No such file or directory")) {

          System.out.println("Diretório do arquivo não encontrado ! ERROR: " + e.getMessage());
          return -10;
        }
      }

    } else {

      try {
        arq = new RandomAccessFile("src/database/futebol.db", "rw");

        testeArquivoVazio = arq.length();

        if (testeArquivoVazio != 0) {

          long tamTotalArq = arq.length();
          long posiI;
          long saveLapide;
          long posiMudar;
          Boolean estouro = false;
          arq.seek(2);
          posicaoRetorno = arq.getFilePointer();
          int tamRegistro = arq.readInt();
          posiI = arq.getFilePointer();
          arq.seek(arq.getFilePointer() + 2);
          saveLapide = arq.getFilePointer();
          arq.seek(saveLapide + 3);
          String nomeR = arq.readUTF();
          // arq.seek(posiI);

          while (estouro == false) {

            if (entrada.equals(nomeR) == true) {
              idencontrado = true;
              arq.seek(saveLapide);
              lapide = arq.readUTF();
              if (lapide.equals(" ")) {
                idDeletado = false;
                estouro = true;
              } else {
                idDeletado = true;
              }
            } else {
              idDeletado = true;
            }

            if (posiI + tamRegistro < tamTotalArq && (idDeletado != false) && (estouro == false)) {
              posiMudar = (long) tamRegistro;
              arq.seek(posiMudar + posiI);
              posicaoRetorno = arq.getFilePointer();
              tamRegistro = arq.readInt();
              posiI = arq.getFilePointer();
              arq.seek(arq.getFilePointer() + 2);
              saveLapide = arq.getFilePointer();
              arq.seek(saveLapide + 3);
              nomeR = arq.readUTF();
              arq.seek(posiI);
            } else {
              estouro = true;
            }

          }

        } else {
          System.out.println("O Arquivo está Vazio, nada para ser Procurado !");
        }
        arq.close();
      } catch (Exception e) {
        String erro = e.getMessage();

        if (erro.contains("No such file or directory")) {

          System.out.println("Diretório do arquivo não encontrado ! ERROR: " + e.getMessage());
          return -10;
        }
      }

    }

    if (idDeletado == true || idencontrado == false || idnexiste == true) {
      posicaoRetorno = -1;
    }

    return posicaoRetorno;

  }

  // --------------------------------------
  // Método procurar clube, tem como parametro uma string e o objeto ft2, ele
  // recebe o nome ou ID a ser procurado e para ser atribuido ao ft2, mas antes
  // ele chama o método pesquisarNoArquivo, que retorna um long que é a posição do
  // primeiro byte de onde começa o Tam do arquivo do registro procurado, se o
  // long for -1 o arquivo procurado não existe e se o long for -10 é que deu erro
  // na pesquisa e esse método retorna essa posição do arquivo para indicar se foi
  // ou não achado o registro no arquivo
  // --------------------------------------

  public long procurarClube(String recebendo, fut ft2) {

    /*
     * como ta sendo feita a escrita
     * ID COMECO DO ARQUIVO + Tam do Arquiv +
     * ARRAYDEBYTE(ID+LAPIDE+NOME+CNPJ+CIDADE+PARTIDASJOGADAS+PONTOS)
     */
    // Escrita no Arquivo

    long retornoPesquisa = pesquisarNoArquivo(recebendo);
    byte[] ba;
    RandomAccessFile arq;

    if (retornoPesquisa >= 0) {

      try {
        arq = new RandomAccessFile("src/database/futebol.db", "rw");
        arq.seek(retornoPesquisa);
        int tamRegistro = arq.readInt();
        ba = new byte[tamRegistro];
        arq.read(ba);
        ft2.fromByteArray(ba);

      } catch (Exception e) {
        String erro = e.getMessage();

        if (erro.contains("No such file or directory")) {

          System.out.println("\nDiretório do arquivo não encontrado ! ERROR: " + e.getMessage());
          return -10;
        } else {
          System.out.println("ERROR: " + e.getMessage());
        }
      }
    } else {
      if (retornoPesquisa == -1) {

        System.out.println("\nRegistro Pesquisado não encontrado !\n");

      }
    }

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
