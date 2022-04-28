import java.io.RandomAccessFile;

public class indice {
  private Short idIndice;
  private long posiIndice;
  private String lapide;

  public indice() {
    lapide = " ";
    idIndice = -1;
    posiIndice = -1;

  }

  public indice(Short id, long posicao) {

    lapide = " ";
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

      if (tamdoArq == 0) {

        tamdoArq = 13;
        arq.seek(tamdoArq);
        arq.writeShort(idIndice);
        arq.writeLong(posiIndice);
        arq.writeUTF(lapide);

      } else {
        boolean ePar = true;
        if ((idIndice) % 2 == 1) {
          ePar = false;
        }

        if (ePar) {
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

  public static void swap(int[] a, int posi1, int posi2) {

    int vTemp = a[posi1];
    a[posi1] = a[posi2];
    a[posi2] = vTemp;

  }

  public static void quicksort(int[] array, int esq, int dir) {

    int i = esq, j = dir;
    int pivo = array[(dir + esq) / 2];
    while (i <= j) {
      while (array[i] < pivo)
        i++;
      while (array[j] > pivo)
        j--;
      if (i <= j) {
        swap(array, i, j);
        i++;
        j--;
      }
    }
    if (esq < j)
      quicksort(array, esq, j);
    if (i < dir)
      quicksort(array, i, dir);

  }

}