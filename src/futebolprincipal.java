import java.util.*;

public class futebolprincipal {

  // --------------------------------------
  // Realizar Partida (Explicação dentro do Método)
  // --------------------------------------

  public static boolean realizarPartida(Scanner entrada) {

    // Esse método retorna true e false para saber se a partida foi relizada com
    // sucesso, ele recebe o nome dos 2 time pede o placar e de acordo com o
    // resultado da partida chama o método arquivoUpdate para atualizar ambos os
    // times no arquivo. Erros foram tratados, caso digite um clube que não exista !

    fut time1 = new fut();
    fut time2 = new fut();
    arquivocrud arq = new arquivocrud();
    Boolean status = false;
    boolean status2 = false;
    entrada = new Scanner(System.in);
    System.out.print("Digite o ID do primeiro clube da partida: ");
    String timeUm = entrada.nextLine();
    System.out.println();
    System.out.print("Digite o ID do SEGUNDO clube da partida: ");
    String timeDois = entrada.nextLine();
    System.out.println();
    boolean retorno = true;

    boolean idOrnot = timeUm.matches("-?\\d+");
    boolean idOrnot2 = timeDois.matches("-?\\d+");
    if (idOrnot && idOrnot2) {
      long retornoTimeUm = arq.procurarClube(timeUm, time1, 0);

      if (retornoTimeUm >= 0) {
        long retornoTimeDois = arq.procurarClube(timeDois, time2, 0);

        System.out.println(time1.toString());
        System.out.println(time2.toString());

        String confirmartimes;
        System.out.println("Confirma que esses são os times que deseja realizar uma partida ? (sim ou nao)");
        confirmartimes = entrada.nextLine();

        if (confirmartimes.toLowerCase().equals("sim")) {

          if (retornoTimeDois >= 0) {

            short placarTimeUm = 0;
            short placarTimeDois = 0;
            byte pontos = 0;

            System.out.println("Digite o placar do Contronto entre o primeiro time e o segundo !");
            System.out.print("Gols Time " + timeUm + ": ");
            placarTimeUm = entrada.nextShort();
            System.out.println();
            System.out.print("Gols Time " + timeDois + ": ");
            placarTimeDois = entrada.nextShort();

            if (placarTimeUm > placarTimeDois) {

              pontos = 3;
              status = arq.arquivoUpdate(timeUm, entrada, "Parcial", pontos, time1);
              pontos = 0;
              status2 = arq.arquivoUpdate(timeDois, entrada, "Parcial", pontos, time2);

            } else {
              pontos = 3;
              if (placarTimeUm < placarTimeDois) {
                pontos = 3;
                status = arq.arquivoUpdate(timeDois, entrada, "Parcial", pontos, time2);
                pontos = 0;
                status2 = arq.arquivoUpdate(timeUm, entrada, "Parcial", pontos, time1);
              } else {
                pontos = 1;
                if (placarTimeUm == placarTimeDois) {
                  status = arq.arquivoUpdate(timeUm, entrada, "Parcial", pontos, time1);
                  status2 = arq.arquivoUpdate(timeDois, entrada, "Parcial", pontos, time2);
                }
              }

            }
          } else {
            System.out.println("Realização de partida CANCELADA, time 2 não encontrado");

            retorno = false;
          }
        } else {
          System.out.println("Partida Cancelada");
          retorno = false;
        }
      } else {
        System.out.println("Realização de partida CANCELADA, time 1 não encontrado !");

        retorno = false;
      }

    } else {
      System.out.println("Para encontrar o Clube para realizar a partida tem que inserir o ID \n");
    }

    if (status == true && status2 == true) {
      System.out.println("Realização de Partida Concluida !");

      System.out.println("------------------X------------------");
      System.out.println(time1.toString());
      System.out.println("------------------X------------------");
      System.out.println(time2.toString());
      System.out.println("------------------X------------------");

    } else {
      System.out.println("Partida NÃO realizada");
      retorno = false;
    }

    return retorno;
  }

  // --------------------------------------
  // Método eNumero: Recebe como parametro duas Strings, a primeira é a que vai
  // ser analiada, e a segunda é para testar se a string testada é do tipo
  // passado, retorna true se for um número e se o numero for do tipo passado e
  // false caso contrario
  // --------------------------------------

  public static boolean eNumero(String str, String tipo) {

    boolean idOrnot = str.matches("-?\\d+");
    boolean verifica = false;
    if (idOrnot == true) {

      if (tipo.equals("Byte") == true) {

        int Inteiro = Integer.parseInt(str);

        if (Inteiro <= 127) {
          verifica = true;
        }

      }

    }

    return verifica;
  }

  public static void main(String[] args) {
    Scanner entrada = new Scanner(System.in);
    arquivocrud arqcru = new arquivocrud();
    fut futebas = new fut();

    arqcru.salvarPrecisaOrdernar(2);
    String menuStr = "";
    byte menu = 0;
    byte zero = 0;
    int menuconvert = 0;
    boolean contador = false;
    boolean eNumero = false;

    while (contador == false) {
      System.out
          .println(
              "0 - Encerrar Programa \n1 - Cadastrar um Clube \n2 - Realizar partida\n3 - Ler um ID do arquivo\n4 - Realizar Atualização de um Registro\n5 - Realizar o Delete de um Registro\n9 - Limpar todos os Arquivos");

      // Esse switch case tem 10 opcoes então foi feito com byte mas para evitar erro
      // ele confere se o numero digitado é do tipo byte com a funcao eNumero;
      menuStr = entrada.nextLine();
      eNumero = eNumero(menuStr, "Byte");

      if (eNumero == true) {
        menuconvert = Integer.parseInt(menuStr);
        menu = (byte) menuconvert;

        switch (menu) {
          case 0:
            contador = true;
            arqcru.salvarPrecisaOrdernar(1);
            System.out.println("Encerrando o programa...");
            break;
          case 1:
            arqcru.criarClube(entrada);
            break;
          case 2:
            // System.out.println("CASE 2 - Realizar Partida");
            realizarPartida(entrada);
            break;
          case 3:

            System.out.println("Digite o ID, Nome do Clube ou Cidade a ser procurado no arquivo");
            String entradaPesquisadeClube = entrada.nextLine();
            futebas.printarClubesExistentes(arqcru.procurarClube(entradaPesquisadeClube, futebas, 0),
                entradaPesquisadeClube);
            break;
          case 4:
            System.out.println("Digite o ID na qual será atualizado os dados !");

            String entradaUpg = entrada.nextLine();
            arqcru.arquivoUpdate(entradaUpg, entrada, "Completo", zero, null);

            break;
          case 5:

            System.out.println("Digite o ID para ser deletado");
            String idDelete = entrada.nextLine();
            arqcru.arquivoDelete(idDelete, entrada, futebas);
            break;

          case 9:
            arqcru.deletaTudo(1, 1, 1, 1, 1, 1, 1);// Método Criado para apagar todo o arquivo
            arqcru.setPrecisarOrdenar(true);
            arqcru.salvarPrecisaOrdernar(1);
            break;

          default:
            System.out.println("Digito Não Valido... Inserir novamente o digito");

        }
        entrada = new Scanner(System.in);// Limpa o Buffer do Scanner
      } else {
        System.out.println("Digito Não Valido... Inserir novamente o digito");

      }

    }

    entrada.close();

  }
}
