
import java.util.Scanner;
import java.io.RandomAccessFile;
import java.io.PrintWriter;

public class arquivocrud {

  private boolean precisaOrdernar = false;

  public boolean getPrecisaOrdernar() {
    return precisaOrdernar;
  }

  public void setPrecisarOrdenar(boolean precisaOrdernar) {// ARRUMAR AQUI DEPOISSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS
    this.precisaOrdernar = precisaOrdernar;
  }

  // --------------------------------------
  // O método salvarPrecisaOrdernar ele salva em um arquivo separado caso seja
  // necessário fazer a ordenação externa novamente, desssa forma gravando quando
  // o arquivo vai estar bagunçado
  // --------------------------------------

  public void salvarPrecisaOrdernar(int op) {
    // op 1 guarda a variavel no arquivo
    // op 2 pega a variavel no arquivo

    try {
      RandomAccessFile arq = new RandomAccessFile("src/database/precisaOrdernar.db", "rw");

      if (op == 1) {
        arq.seek(0);
        arq.writeBoolean(precisaOrdernar);
      } else {
        if (op == 2) {
          arq.seek(0);
          this.precisaOrdernar = arq.readBoolean();
        }
      }
      arq.close();
    } catch (Exception e) {
      System.out.println("Erro no salvarPrecisaOrdernar: " + e.getCause());
    }

  }

  // --------------------------------------
  // Método deletaTudo é um método que apaga todos os arquivos de dados .db !
  // --------------------------------------
  public void deletaTudo(int valor, int valor1, int valor2, int valor3, int valor4, int valor5, int valor6) {

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

      if (valor5 == 1) {

        PrintWriter writer2 = new PrintWriter("src/database/aindices.db");

        writer2.print("");
        writer2.close();

      }

      if (valor6 == 1) {
        PrintWriter writer = new PrintWriter("src/database/listainvertida.db");

        writer.print("");
        writer.close();

      }

    } catch (Exception e) {
      System.out.println("ERRO NO DELETA TUDO");
    }

  }
  // ----------------------------------------X----------------------------------------//

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
      listainvertida li = new listainvertida();
      arq = new RandomAccessFile("src/database/futebol.db", "rw");

      String nomeSalvarLI = ft.getNome();
      li.setNomeLista(nomeSalvarLI);
      long salvarPosiLI = 0;
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
      salvarPosiLI = finaldoarquivo;
      // System.out.println(arq.getFilePointer());

      ft.setIdClube(idcabecalhosave);
      ba = ft.toByteArray();
      arq.writeInt(ba.length);
      arq.write(ba);

      // local que faz a escrita no arquivo
      ic.setIdIndice(idcabecalhosave);
      ic.setPosiIndice(posiIndice);
      ic.writeIndicetoArq();

      // fazer insercao na lista....
      li.setPosiArqPrinc(salvarPosiLI);
      li.escreverListaInvertida(ft.getNome());

      if (!(ft.getCidade().equals(""))) {
        li.setNomeLista(ft.getCidade());
        li.escreverListaInvertida(ft.getCidade());
      }

    } catch (Exception e) {
      String erro = e.getMessage();

      if (erro.contains("No such file or directory")) {

        System.out.println("Diretório do arquivo não encontrado !");
        return;
      }

    }
    setPrecisarOrdenar(true);
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
    setPrecisarOrdenar(true);
  }

  // -------------------Create - FIM---------------------------------//

  // ----------------------READ-------------------------//

  // --------------------------------------
  // Para criar a ordenação externa ser bem executada criou um modo de
  // embaralhamento na qual salva os indices pares primeiros e depois os impares,
  // porém quando ele faz esse salto ele deixa um gap(buraco) de zero, no meio do
  // arquivo e essa funcao identifica esses gaps para correção
  // --------------------------------------

  public boolean temMargemZero() {
    boolean r = false;
    try {

      RandomAccessFile arq = new RandomAccessFile("src/database/aindices.db", "r");

      arq.seek(arq.length() - 13);
      short temShort = arq.readShort();

      if (temShort == 0) {
        r = true;
      }

      arq.close();
    } catch (Exception e) {
      System.out.println("Erro na function temMargemZero: " + e.getCause());
    }
    return r;
  }

  // --------------------------------------
  // Método pesquisaBinariaArquivoIndice faz a busca binaria no arquivo de
  // indices, dessa forma retornando a posicao long no arquivo de dados, e logo
  // ja testa se o arquivo está deletado ou não. OBS esse método só faz a procura
  // de números.
  // --------------------------------------
  public long pesquisaBinariaArquivoIndice(int n, boolean retornarArqIndiceOuDados) {

    // se for para retornar posicao do arquivo de dados é 1, se for do proprio
    // arquivo de indice é 0;
    long posicaoRetorno = -1;
    String lapide = "*";
    try {
      RandomAccessFile arq = new RandomAccessFile("src/database/aindices.db", "r");

      if (retornarArqIndiceOuDados) {// pegar o indice que ta salvar aqui junto com a lapide e o id entao o indice é
                                     // referente ao arquivo de dados.

        if (arq.length() != 0) {

          arq.seek(0);
          int esq = arq.readShort();
          int qtdElementos = (int) arq.length() / 13;

          arq.seek(arq.length() - 13);
          long dirconvert = arq.length() / 13;
          int dir = (int) dirconvert;

          int numerodoMeio = 0;

          int mid = (esq + dir) / 2;
          arq.seek(0);

          while (esq <= dir) {
            mid = (esq + dir) / 2;

            arq.seek(((mid) * 13) - 13);

            if (arq.getFilePointer() < arq.length()) {

              numerodoMeio = arq.readShort();

              if (n == numerodoMeio) {

                posicaoRetorno = arq.readLong();
                String testelapide = arq.readUTF();

                if (testelapide.equals(lapide)) {
                  posicaoRetorno = -1;

                }
                esq = qtdElementos + 100;
              } else if (n > numerodoMeio) {

                esq = mid + 1;
              } else {

                dir = mid - 1;
              }

            } else {
              esq = qtdElementos + 100;
            }
          }
        } else {
          System.out.println("ERROR: O arquivo de busca se encontra vazio !");
          posicaoRetorno = -10;
        }

      } else {
        if (arq.length() != 0) {

          arq.seek(0);
          int esq = arq.readShort();
          int qtdElementos = (int) arq.length() / 13;

          arq.seek(arq.length() - 13);
          long dirconvert = arq.length() / 13;
          int dir = (int) dirconvert;

          int mid = (esq + dir) / 2;
          arq.seek(0);

          while (esq <= dir) {
            mid = (esq + dir) / 2;

            arq.seek(((mid) * 13) - 13);
            if (arq.getFilePointer() < arq.length()) {
              int numerodoMeio = arq.readShort();
              if (n == numerodoMeio) {
                posicaoRetorno = arq.getFilePointer() - 2;
                String testelapide = arq.readUTF();

                if (testelapide.equals(lapide)) {
                  posicaoRetorno = -1;
                  // dir = mid - 1;
                }
                esq = qtdElementos + 1;

              } else if (n > numerodoMeio) {

                esq = mid + 1;
              } else {
                dir = mid - 1;
              }
            }
          }

        } else {
          System.out.println("ERROR: O arquivo de busca se encontra vazio !");
          posicaoRetorno = -10;
        }
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
    // pesquisa para retornar a posicao do id procurado no arquivo de indice

  }

  // --------------------------------------
  // COMENTA NOVAMENTE PROCURAR CLUBE POIS FOI FUNDIDO COM O METODO
  // pesquisarNoArquivo
  // --------------------------------------

  public long procurarClube(String recebendo, fut ft2, int metodo) {// ARRRRRUMARRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

    /*
     * como ta sendo feita a escrita
     * ID COMECO DO ARQUIVO + Tam do Arquiv +
     * ARRAYDEBYTE(ID+LAPIDE+NOME+CNPJ+CIDADE+PARTIDASJOGADAS+PONTOS)
     */
    // Escrita no Arquivo
    long retornoPesquisa = -1;
    boolean idOrnot = recebendo.matches("-?\\d+");
    String lapide = "*";

    ordenacaoexterna oe = new ordenacaoexterna();

    if (idOrnot == true) {// Inicio Pesquisa Númerica

      if (precisaOrdernar == true) {
        oe.ordenacaoDistribuicao();
      }

      int entradaInt = Integer.parseInt(recebendo);
      retornoPesquisa = pesquisaBinariaArquivoIndice(entradaInt, true);// chama a pesquisa binária

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

          arq.close();

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
      setPrecisarOrdenar(false);
    } else {// aqui é quando se faz a pesquisa por nome ou cidade do clube

      if (metodo != 5 && metodo != 4) {

        listainvertida li = new listainvertida();
        retornoPesquisa = li.pesquisaListaInvertida(recebendo, true);
        if (retornoPesquisa == -1) {

          System.out.println("\nRegistro Pesquisado não encontrado !\n");

        }
      } else {
        System.out
            .println(
                "Não pode deletar ou atualizar um Registro a partir do seu nome, tem que deletar a partir do seu ID !!!\n");
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
  // --------------------------------------
  // O método arquivoDeleteNaListaInvertida, ele faz em um método separadamente o
  // delete na lista invertida, lembrando que na Lista invertida, o delete
  // consiste em apagar o bytes e gravar um conjunto de zero por cima e não marcar
  // a lapide
  // --------------------------------------
  public boolean arquivoDeleteNaListaInvertida(String nomedoDelete, long posicaoNoArquivoDeDados) {

    RandomAccessFile arqLInvertida;
    boolean estaDeletado = false;

    listainvertida li = new listainvertida();
    try {

      long receberPosicaoDoNomeNaListaInvertida = li.pesquisaListaInvertida(nomedoDelete, false);

      arqLInvertida = new RandomAccessFile("src/database/listainvertida.db", "rw");
      arqLInvertida.seek(receberPosicaoDoNomeNaListaInvertida);

      String nomeLI = arqLInvertida.readUTF();

      if (nomeLI.equals(nomedoDelete)) {
        long pegarOsIndices = 0;
        boolean marcador2 = true;
        long posiAntesdoReadLong = 0;
        long lapidezero = 0;
        posiAntesdoReadLong = arqLInvertida.getFilePointer();
        pegarOsIndices = arqLInvertida.readLong();
        while (pegarOsIndices != -10 && marcador2) {

          if (pegarOsIndices == posicaoNoArquivoDeDados) {

            arqLInvertida.seek(posiAntesdoReadLong);
            arqLInvertida.writeLong(lapidezero);
            marcador2 = false;
            estaDeletado = true;

          }
          posiAntesdoReadLong = arqLInvertida.getFilePointer();
          pegarOsIndices = arqLInvertida.readLong();
        }

      }

    } catch (Exception e) {
      System.out.println("Erro no Delete no Arquivo da Lista Invertida");
    }
    return estaDeletado;
  }

  public void arquivoDelete(String id, Scanner verificarultimoDelete, fut ft2) {

    RandomAccessFile arqP;
    RandomAccessFile arqIndice;
    String lapide = "";
    boolean arquivoDeletado = false;
    try {
      arqP = new RandomAccessFile("src/database/futebol.db", "rw");
      arqIndice = new RandomAccessFile("src/database/aindices.db", "rw");

      long idExist = procurarClube(id, ft2, 5);

      if (idExist >= 0) {

        System.out.println(ft2.toString());

        System.out.println("Você deseja deletar esse registro ?");
        String ultVeri = verificarultimoDelete.nextLine();

        if ((ultVeri.toLowerCase().equals("sim") == true)) {
          arqP.seek(idExist + 6);
          arqIndice.seek(ft2.getIdClube() * 10);
          lapide = "*";
          // System.out.println(arqP.getFilePointer());
          // System.out.println(arqIndice.getFilePointer());
          int convertIdstrtoInt = Integer.parseInt(id);
          long receberPosiArqIndice = pesquisaBinariaArquivoIndice(convertIdstrtoInt, false);
          arqIndice.seek(receberPosiArqIndice + 10);
          arqP.writeUTF(lapide);
          arqIndice.writeUTF(lapide);

          // mandar para o delete lista invertida, mandar
          boolean deleteAI = arquivoDeleteNaListaInvertida(ft2.getNome(), idExist);
          boolean deleteAI2 = false;
          if ((!ft2.getNome().equals(""))) {
            deleteAI2 = arquivoDeleteNaListaInvertida(ft2.getCidade(), idExist);
          }

          if (deleteAI && deleteAI2) {
            arquivoDeletado = true;
          }

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
  // --------------------------------------
  // O método arquivoLIUpdate, faz o update da lista invertida, dessa maneira
  // sempre que for atualizar pra um arquivo maior ou menor que o anterior, tem q
  // atualizar a lista invertida, ao contrario do arquivo de index que so contem
  // de informacao o id e o posicionamento, e quando um registro atualizado novo
  // for menor que o antigo ele pode ocupar a msm posicao não interferindo no
  // arquivo de indices
  // --------------------------------------
  public void arquivoLIUpdate(long posiNOVAdoRegistro, String nomeUpdate, long posicaoNoArquivoDeDados, fut futebas,
      String palavraParaAdicionarnaLista) {

    RandomAccessFile arqLInvertida;

    listainvertida li = new listainvertida();

    try {

      arqLInvertida = new RandomAccessFile("src/database/listainvertida.db", "rw");

      long receberPosicaoDoNomeNaListaInvertida = li.pesquisaListaInvertida(nomeUpdate, false);

      if (receberPosicaoDoNomeNaListaInvertida != -1) {

        arqLInvertida.seek(receberPosicaoDoNomeNaListaInvertida);
        String nomeparaLer = arqLInvertida.readUTF();
        long zero = 0;
        if (nomeparaLer.equals(nomeUpdate)) {
          boolean marcador2 = true;
          long salvarPosiAntesdoReadLong = arqLInvertida.getFilePointer();
          long pegarOsIndices = arqLInvertida.readLong();
          while (pegarOsIndices != -10 && marcador2) {

            if (pegarOsIndices == posicaoNoArquivoDeDados) {

              arqLInvertida.seek(salvarPosiAntesdoReadLong);
              arqLInvertida.writeLong(zero);
              marcador2 = false;
            }
            salvarPosiAntesdoReadLong = arqLInvertida.getFilePointer();
            pegarOsIndices = arqLInvertida.readLong();
          }

        }
      }

      if (!(palavraParaAdicionarnaLista.equals(""))) {
        li.setNomeLista(palavraParaAdicionarnaLista);
        li.setPosiArqPrinc(posiNOVAdoRegistro);
        li.escreverListaInvertida(palavraParaAdicionarnaLista);
      }

    } catch (Exception e) {
      System.out.println("Error no arquivoLIUpdate, ERRO: " + e.getCause());
    }

  }

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
    RandomAccessFile arqIndi;

    if (tipoDeUpdate.equals("Completo")) {
      fut ft2 = new fut();
      indice idc;
      long receberProcura = procurarClube(nomeidProcurado, ft2, 4);
      byte[] ba;
      String stgConfirma = "";

      if (receberProcura >= 0) {

        System.out.println("Você deseja Atualizar o Registro abaixo ?");
        System.out.println(ft2.toString());
        String salvarFt2nomeAntigop = ft2.getNome();
        String salvarFt2cidadeAntigop = ft2.getCidade();
        System.out.print("Inserir Resposta (SIM OU NAO): ");
        stgConfirma = entradaUpdate.nextLine();

        if (stgConfirma.toUpperCase().equals("SIM")) {

          try {
            arq = new RandomAccessFile("src/database/futebol.db", "rw");
            arqIndi = new RandomAccessFile("src/database/aindices.db", "rw");
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

              // fazer atualizacao na lista invertida
              String palavraParaAdicionarnaLista = ft2.getNome();
              arquivoLIUpdate(receberProcura, salvarFt2nomeAntigop, receberProcura, ft2,
                  palavraParaAdicionarnaLista);
              palavraParaAdicionarnaLista = ft2.getCidade();
              if (!(salvarFt2cidadeAntigop.equals(""))) {
                arquivoLIUpdate(receberProcura, salvarFt2cidadeAntigop, receberProcura, ft2,
                    palavraParaAdicionarnaLista);
              }

              System.out.println("Arquivo Escrito com Sucesso !");

            } else {
              arq.seek(0);
              // peganto tam total do arq
              long tamanhoTotalArq = arq.length();
              long longArquivoIndice = tamanhoTotalArq;
              // pegando Id do cabecalho
              arq.seek(receberProcura + 4);
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
              long posiNOVAdoRegistro = tamanhoTotalArq;

              pegarPrimeiroId++;
              arq.seek(0);
              arq.writeShort(pegarPrimeiroId);
              short salvarIdnoIndice = pegarPrimeiroId;

              ft2.setIdClube(pegarPrimeiroId);

              arq.seek(tamanhoTotalArq);
              ba = ft2.toByteArray();
              arq.writeInt(ba.length);
              arq.write(ba);

              // arq.seek(0);
              // arq.writeShort(pegarPrimeiroId);

              // mudar registro no arquivo de indice
              int convertIdStringtoInt = Integer.parseInt(nomeidProcurado);
              long receberPosiArqIndice = pesquisaBinariaArquivoIndice(convertIdStringtoInt, false);

              arqIndi.seek(receberPosiArqIndice + 10);
              arqIndi.writeUTF("*");

              idc = new indice();
              idc.setIdIndice(salvarIdnoIndice);
              idc.setPosiIndice(longArquivoIndice);
              idc.setLapide(" ");
              idc.writeIndiceLastPosi();
              setPrecisarOrdenar(true);

              String palavraParaAdicionarnaLista = ft2.getNome();

              // fazer atualizacao na lista invertida
              arquivoLIUpdate(posiNOVAdoRegistro, salvarFt2nomeAntigop, receberProcura, ft2,
                  palavraParaAdicionarnaLista);
              palavraParaAdicionarnaLista = ft2.getCidade();
              if (!(salvarFt2cidadeAntigop.equals(""))) {
                arquivoLIUpdate(posiNOVAdoRegistro, salvarFt2cidadeAntigop, receberProcura, ft2,
                    palavraParaAdicionarnaLista);
              }

              System.out.println("Arquivo Atualizado com Sucesso ! \n");
            }
            System.out.println("----------X----------\n");
            System.out.println("Novo registro agora ficou assim: ");
            System.out.println(ft2.toString());

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

        long receberProcura = procurarClube(nomeidProcurado, futebasParcial, 0);
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