import java.io.*;

public class fut {

  private String lapide = " ";
  private short idClube = 1;
  private String nome = "";
  private String cnpj = "";
  private String cidade = "";
  private byte partidasJogadas = 0;
  private byte pontos = 0;

  public fut() {// Método Construtor 1
    lapide = " ";
    idClube = 0;
    nome = " ";
    cnpj = " ";
    cidade = " ";
    partidasJogadas = 0;
    pontos = 0;

  }

  public fut(String nomeC, String cnpjC, String cidadeC) {// Método Contrutor 2
    lapide = " ";
    idClube = 0;
    nome = nomeC;
    cnpj = cnpjC;
    cidade = cidadeC;
    partidasJogadas = 0;
    pontos = 0;

  }

  // ------------------------X-Métodos-Geters-e-Sets-X------------------------//

  public String getLapide() {
    return lapide;
  }

  public short getIdClube() {
    return idClube;
  }

  public String getNome() {
    return nome;
  }

  public String getCnpj() {
    return cnpj;
  }

  public String getCidade() {
    return cidade;
  }

  public byte getPartidasJogadas() {
    return partidasJogadas;
  }

  public byte getPontos() {
    return pontos;
  }

  public void setLapide(String lapide) {
    this.lapide = lapide;
  }

  public void setIdClube(short idClube) {
    this.idClube = idClube;
  }

  public void setCidade(String cidade) {

    this.cidade = cidade;
  }

  // --------------------------------------
  // o setCnpj so ira setar um CPNJ caso ele seja valido, caso contrario continua
  // vazio
  // --------------------------------------
  public void setCnpj(String cnpjRC) {

    if (verificaCPNJ(cnpjRC)) {
      this.cnpj = cnpjRC;
    } else {

      if (!(cnpjRC.equals(""))) {
        System.out.println("CNPJ NÃO VALIDO");
      }

      this.cnpj = "";
    }

  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public void setPartidasJogadas(byte partidasJogadas) {
    this.partidasJogadas = partidasJogadas;
  }

  public void setPontos(byte pontos) {
    this.pontos = pontos;
  }

  // ------------------------X-FIM-Métodos-Geters-e-Sets-X------------------------//
  // --------------------------------------
  // Método para passar a classe para array de bytes
  // --------------------------------------
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeShort(idClube);
    dos.writeUTF(lapide);
    dos.writeUTF(nome);
    dos.writeUTF(cnpj);
    dos.writeUTF(cidade);
    dos.writeByte(partidasJogadas);
    dos.writeByte(pontos);
    return baos.toByteArray();
  }

  // --------------------------------------
  // Método para ler um byte array e passar para a classe
  // --------------------------------------
  public void fromByteArray(byte[] byteArray) throws IOException {

    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bais);
    idClube = dis.readShort();
    lapide = dis.readUTF();
    nome = dis.readUTF();
    cnpj = dis.readUTF();
    cidade = dis.readUTF();
    partidasJogadas = dis.readByte();
    pontos = dis.readByte();

  }

  // --------------------------------------
  // Método verificaCNPJ recebe uma string e avalia se todos os caracteres
  // são compativeis com um CNPJ, se for igual um CNPJ retorna true caso contratio
  // false;
  // --------------------------------------
  public boolean verificaCPNJ(String cnpj) {

    boolean verificador = false;
    int tamcnpj = cnpj.length();

    if (tamcnpj == 18) {

      for (int i = 0; i < tamcnpj; i++) {

        char a = cnpj.charAt(i);

        if ((i >= 0 && i <= 1) || (i >= 3 && i <= 5) || (i >= 7 && i <= 9) || (i >= 11 && i <= 14)
            || (i >= 16 && i <= 17)) {

          if (!(a >= 48 && a <= 57)) {
            verificador = false;
            return verificador;
          }

        }

        if (i == 2) {

          if (!(a == 46)) {
            verificador = false;
            return verificador;
          }

        }

        if (i == 6) {
          if (!(a == 46)) {
            verificador = false;
            return verificador;
          }
        }

        if (i == 10) {
          if (!(a == 47)) {
            verificador = false;
            return verificador;
          }
        }

        if (i == 15) {
          if (!(a == 45)) {
            verificador = false;
            return verificador;
          }
        }
      }
      verificador = true;
    }

    return verificador;

  }

  // --------------------------------------
  // Método printarClubesExistentes, é um método verificador para chamar o to
  // String, pois o método de pesquisa retorna um long, caso ele seja maior ou
  // igual a 0, o registro existe se for -1 ele não existe e se for -10 é um erro,
  // então so pode ser impresso caso seja >= 0
  // --------------------------------------
  public void printarClubesExistentes(long existeClube, String entrada) {

    boolean idOrnot = entrada.matches("-?\\d+");

    if (existeClube >= 0 && idOrnot) {
      System.out.println(toString());
    }

  }

  // --------------------------------------
  // Método de Impressao
  // --------------------------------------
  public String toString() {
    return "\n" + "ID do Clube: " + getIdClube() + "\n" + "Nome do Clube: " + getNome() + "\n" + "CNPJ: "
        + getCnpj()
        + "\n" + "CIDADE do Clube: " + getCidade() + "\n" + "QTD de Partidas Jogadas: " + getPartidasJogadas() + "\n"
        + "QTD de Pontos: " + getPontos() + "\n";

  }

}