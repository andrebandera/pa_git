package br.gov.ms.detran.processo.administrativo.wrapper;

import java.io.Serializable;

public class RecursoOnlineCanceladoWrapper implements Serializable {

    private String funcionalidade;
    private final String ip;
    private final String cpfUsuario;

    public RecursoOnlineCanceladoWrapper(String funcionalidade, String ip, String cpfUsuario) {
        this.funcionalidade = funcionalidade;
        this.ip = ip;
        this.cpfUsuario = cpfUsuario;
    }

    public String getFuncionalidade() {
        return funcionalidade;
    }

    public String getIp() {
        return ip;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setFuncionalidade(String funcionalidade) {
        this.funcionalidade = funcionalidade;
    }
}
