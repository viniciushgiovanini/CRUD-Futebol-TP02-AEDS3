import java.io.*;

public class indice {
  private Short idIndice;
  private long posiIndice;
  private String lapide;

  public indice() {
    lapide = " ";
    idIndice = -1;
    posiIndice = -1;

  }

  public indice(Short id, long posicao, String lapide) {

    this.lapide = lapide;
    idIndice = id;
    posiIndice = posicao;

  }

  public Short getIdIndice() {
    return idIndice;
  }

  public String getLapide() {
    return lapide;
  }

  public long getPosiIndice() {
    return posiIndice;
  }

  public void setIdIndice(Short idIndice) {
    this.idIndice = idIndice;
  }

  public void setLapide(String lapide) {
    this.lapide = lapide;
  }

  public void setPosiIndice(long posiIndice) {
    this.posiIndice = posiIndice;
  }

  // --------------------------------------
  // O método writeIndicetoArq é o write principal, pois ele procura quando se
  // cria os registros, salvar ele de forma desordenada no arquivo, para gerar uma
  // desordenação a mais, ele faz isso salvando numeros pares antes dos impares
  // O normal seria 1------2------3------4
  // O método faz 2------1------4------3
  // --------------------------------------
  public void writeIndicetoArq() {

    // -------------------Create---------------------------------//
    // --------------------------------------
    // Método escreverIndice, faz a lista de indices e sua respectiva posicao no
    // arquivo que é um número long
    // ESCREVE SHORT + LONG = 10 BYTES POR ESCRITA
    // --------------------------------------

    // 0------10--------20-------30-------40------50
    // 1 0 3 2 4
    // os indices sao invertidos para o ordenacao fazer efeito se nao, nao teria
    // sentido fazer a ordenação.

    try {
      RandomAccessFile arq = new RandomAccessFile("src/database/aindices.db", "rw");
      long tamdoArq = arq.length();
      arquivocrud arqcru = new arquivocrud();

      if (tamdoArq == 0) {

        tamdoArq = 13;
        arq.seek(tamdoArq);
        arq.writeShort(idIndice);
        arq.writeLong(posiIndice);
        arq.writeUTF(lapide);

      } else {
        boolean ePar = true;

        if (arqcru.temMargemZero() == true) {

          arq.seek(tamdoArq - 13);
          arq.writeShort(idIndice);
          arq.writeLong(posiIndice);
          arq.writeUTF(lapide);

        } else {

          if ((idIndice) % 2 == 1) {
            ePar = false;
          }

          if (!ePar) {
            arq.seek(tamdoArq + 13);
            arq.writeShort(idIndice);
            arq.writeLong(posiIndice);
            arq.writeUTF(lapide);
          } else {
            arq.seek(tamdoArq - 26);
            // System.out.println(arq.getFilePointer());
            arq.writeShort(idIndice);
            arq.writeLong(posiIndice);
            arq.writeUTF(lapide);
          }

        }
      }

      /*
       * para salvar o indice sequencialmente de forma crescente
       * arq.seek(tamdoArq);
       * arq.writeShort(id);
       * arq.writeLong(posicao);
       */

      arq.close();
    } catch (Exception e) {

      String error = e.getMessage();
      System.out.println(error);
      return;

    }

  }

  // --------------------------------------
  // O método writeIndiceLastPosi foi feito pois foi necessário no update salvar
  // um novo Indice na ultima posicao do arquivo, não podendo utilizar os
  // criterios randomicos do primeiro método
  // --------------------------------------

  public void writeIndiceLastPosi() {

    try {
      RandomAccessFile arq = new RandomAccessFile("src/database/aindices.db", "rw");
      long tamdoArq = arq.length();
      arquivocrud arqcru = new arquivocrud();

      if (arqcru.temMargemZero() == true) {

        arq.seek(tamdoArq - 13);
        arq.writeShort(idIndice);
        arq.writeLong(posiIndice);
        arq.writeUTF(lapide);

      } else {

        arq.seek(tamdoArq);
        arq.writeShort(getIdIndice());
        arq.writeLong(getPosiIndice());
        arq.writeUTF(getLapide());
      }
      /*
       * para salvar o indice sequencialmente de forma crescente
       * arq.seek(tamdoArq);
       * arq.writeShort(id);
       * arq.writeLong(posicao);
       */

      arq.close();
    } catch (Exception e) {

      String error = e.getMessage();
      System.out.println(error);
      return;

    }

  }

  // faz o swap para ordenar a distribuicao
  public static void swapIndice(indice[] a, int posi1, int posi2) {
    indice vTemp[] = new indice[1];

    vTemp[0] = a[posi1];
    a[posi1] = a[posi2];
    a[posi2] = vTemp[0];

  }

  // quicksort da distribuicao da ordenação externa
  public void quicksortIndice(indice[] array, int esq, int dir) {

    int i = esq, j = dir;
    int pivo = array[(dir + esq) / 2].idIndice;
    while (i <= j) {
      while (array[i].idIndice < pivo)
        i++;
      while (array[j].idIndice > pivo)
        j--;
      if (i <= j) {
        swapIndice(array, i, j);
        i++;
        j--;
      }
    }
    if (esq < j)
      quicksortIndice(array, esq, j);
    if (i < dir)
      quicksortIndice(array, i, dir);

  }

  // faz um toByteArray para salvar o array de indice no arquivo (distribuicao -
  // ordenação externa)
  public byte[] toByteArray(indice[] ic, int qtdX) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    try {

      int contador = 0;

      while (contador < qtdX) {
        dos.writeShort(ic[contador].idIndice);
        dos.writeLong(ic[contador].posiIndice);
        dos.writeUTF(ic[contador].lapide);
        contador++;
      }

    } catch (Exception e) {
      String error = e.getMessage();
      System.out.println("Mensagem de error: " + error);

    }
    return baos.toByteArray();
  }

}