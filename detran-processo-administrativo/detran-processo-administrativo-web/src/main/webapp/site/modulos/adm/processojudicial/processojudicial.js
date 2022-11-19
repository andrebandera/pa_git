
angular.module('processojudicial',['ngRoute'])
    
.constant("processojudicialConfig",{
    name: 'processojudicial',
    urls: {
        "pesquisar":'detran-processo-administrativo/resource/dadoprocessojudicials/search',
        "gravar":'detran-processo-administrativo/resource/dadoprocessojudicials/save',
        "desativar":'detran-processo-administrativo/resource/dadoprocessojudicials/',
        "reativar":'detran-processo-administrativo/resource/dadoprocessojudicials/reativar',
        "novo":'detran-processo-administrativo/resource/dadoprocessojudicials/new',
        "editar":'detran-processo-administrativo/resource/dadoprocessojudicials/editar',
        "novapessoa": "pessoafisicas/new",
        "carregarprocessos":'detran-processo-administrativo/resource/dadoprocessojudicials/carregarprocessos',
        "buscarOrgaoJudicial":'detran-processo-administrativo/resource/dadoprocessojudicials/buscarOrgaoJudicial',
    },
    model:[
        {field:"entidade.id", hidden: "hide"},
        {field:"entidade.processoJudicial.isn", displayName:"processojudicial.label.isn" },
        {field:"entidade.ativoLabel", displayName:"modulo.global.label.ativoLabel" },	
        {field: "actions", actions: [
          {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"},
          {name: "editar", icon: "global.btn.editar.icon", btn:"global.btn.editar.btn", title:"global.btn.editar.tooltip"},
          {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title:"global.btn.desativar.tooltip"},
          {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title:"global.btn.reativar.tooltip"}
        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/processojudicial',
    urlPath: '/adm/processojudicial',
    i18nextResources : {
        "processojudicial" : {
            "titulo": "Processo Judicial",
            "label" : {
                "isn": "ISN",
                "cpf": "CPF",
                "tipoDocumento": "Tipo Documento",
                "valorDocumento": "Valor Documento",
                "parte": "Parte",
                "formularioRenach": "Formulário Renach",
                "tipoMedida": "Tipo Medida",
                "valorAutos": "Valor Autos",
                "orgaoJudicial": "Órgão Judicial",
                "orgaoBca": "Órgão BCA",
                "requisitoCursoBloqueio": "Requisito Curso Bloqueio",
                "unidadePenal": "Unidade Penal",
                "prazoPenalidade": "Prazo Penalidade",
                "identificacaoRecolhimentoCnh": "Identificação Recolhimento CNH",
                "dataEntregaCnh": "Data Entrega CNH",
                "dataBloqueio": "Data Bloqueio",
                "observacao": "Observação",
                "identificacaoDelito": "Identificação Delito",
                "tribunal": "Tribunal",
                "indicativoPrazoIndeterminado": "Prazo Indeterminado",
                "tipoProcesso": "Tipo Processo",
                "dataInicioPenalidade": "Data Início Penalidade",
                "segredoJustica": "Segredo Justiça",
                "indicativoProva": "Indicativo Prova",
                "msgCnhEntregue": "Atenção!"
            }
        },
        "pessoa": {
                 "titulo": "Cadastro Pessoa",
                    "label": {
                        "nome": "Nome",
                        "perfil": "Perfil",
                        "matricula": "Matrícula",
                        "orgaoEmissorEnumRG": "Orgão Emissor",
                        "estadoEmissorEnumRG": "Estado Emissor",
                        "postoAtendimento": {"descricao": "Posto Atendimento"},
                        "desenvolvedor": "Desenvolvedor",
                        "rg": "Documento",
                        "numeroDetran": "Nº Detran",
                        "orgao": "Órgão/Empresa",
                        "cpf": "CPF",
                        "dataNasc": "Data Nascimento",
                        "email": "E-mail",
                        "telefone": "Telefone",
                        "tipoContato": "Tipo Contato",
                        "tipoDocumento": "Tipo Documento",
                        "tipoEmpresa": "Tipo Empresa",
                        "documentos": "Documentos Digitalizados",
                        "documentousuario": "Documentos Digitalizados do Usuário",
                        "impressora": "Impressora da Rede",
                        "menuBuscaRapida": "Filtro Menu",
                        "alterarPosto": "Alterar Posto Atendimento",
                        "acessoSuporte": "Acesso Completo ao Manual",            
                    }}
    }
})

.config(["$routeProvider", "processojudicialConfig", function ($rp, config) {
    var path = config.filesPath;
    var urlPath = config.urlPath;
    var name = config.name;
    
    $rp
    .when( urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'processojudicial.titulo'})
    .when( urlPath + '/novo', {templateUrl: path + '/index.html', controller: name + 'Create', label: 'global.label.breadcrumb.novo'})
    .when( urlPath + '/ver', {templateUrl: path + '/form.html', controller: name + 'View', label: 'global.label.breadcrumb.ver'})
    .when( urlPath + '/editar', {templateUrl: path + '/form.html', controller: name + 'Edit', label: 'global.label.breadcrumb.editar'});
}])

.controller('processojudicialCtrl', ["$scope", "$injector", "processojudicialConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('processojudicialList', ['$scope', '$controller', function($scope, $controller){
        $scope.page = 'list';
        $controller('processojudicialCustomCtrl', {$scope: $scope});

        $scope.load();
    }
])

.controller('processojudicialCreate', ['$scope', '$controller', function($scope, $controller){
        $scope.page = 'create';
        $controller('processojudicialCustomCtrl', {$scope: $scope});
    }
])

.controller('processojudicialView', ['$scope', '$controller', function($scope, $controller){
        $scope.page = 'view';
        $controller('processojudicialCustomCtrl', {$scope: $scope});
    }
])

.controller('processojudicialEdit', ["$scope", "$controller", '$rootScope',
    function($scope, $controller, $rootScope){
        $scope.page = 'edit';
        $controller('processojudicialCustomCtrl', {$scope: $scope});
    }
])

.controller('processojudicialCustomCtrl', [
"$scope", "$controller", "processojudicialConfig", "$rootScope", "$injector", "$location", "srvDetranAbstractResourceRest", "utils", "growl", "detranModal",
    function($scope, ctrl, config, $rootScope, $injector, $location, srvRest, utils, growl, dtnModal){
        ctrl('processojudicialCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        $scope.data.entidade = {};
        $scope.data.entidade.unidadePenal = 'DIA';
        $scope.existePessoaNoSistema = false;
        $scope.bloquearValorDocumentoPju = false;
        $scope.desabilitaTipoProcesso = false;
        $scope.buscaRealizada = false;
        $scope.dataModal = {};
        $scope.criteria = {};
        $scope.identificacaoRecolhimentoCnh = null;
        
        if ($scope.page === 'view'|| $scope.page === 'edit') {
             //Carregar dados
             if(!angular.isUndefined($rootScope.idPaginaAtual)){
                $scope.editar($scope.urls.pesquisar, '{"id":"'+ $rootScope.idPaginaAtual +'"}').then(function(retorno){            
                    $scope.data = retorno.entity[0];
                });
             }
        }

        $scope.validarForm = function(){
            if(detranUtil.ehBrancoOuNulo($scope.data)
                    || detranUtil.ehBrancoOuNulo($scope.data.entidade)
                    || detranUtil.ehBrancoOuNulo($scope.data.entidade.segredoJustica)){
                return true;
            }
            return false;
        };

        $scope.preSave = function() {
            if (angular.isDefined($scope.data.tipoDocumento) && $scope.data.tipoDocumento != '' && $scope.data.tipoDocumento != null)
                $scope.data.tipoDocumento = utils.convertSelectItemToObject($scope.data.tipoDocumento);
            
            if (angular.isDefined($scope.data.orgaoJudicialEntidade) && $scope.data.orgaoJudicialEntidade != '' && $scope.data.orgaoJudicialEntidade != null)
                $scope.data.orgaoJudicialEntidade = utils.convertSelectItemToObject($scope.data.orgaoJudicialEntidade);
            
            if (angular.isDefined($scope.data.orgaoBcaEntidade) && $scope.data.orgaoBcaEntidade != '' && $scope.data.orgaoBcaEntidade != null)
                $scope.data.orgaoBcaEntidade = utils.convertSelectItemToObject($scope.data.orgaoBcaEntidade);
            
            if (angular.isDefined($scope.data.entidade)) {
                $scope.data.entidade.unidadePenal = 'DIA';
            }
            
            if($scope.validarGravacao())
                $scope.save();
        };
        
        $scope.$on('savedData' + config.name, function(event, data){
            $scope.limparCampos();
        });
        
        $scope.limparCampos = function(){
            $scope.limparCamposProcessoJudicial();
            $scope.criteria = {};
        }
        
        $scope.validarGravacao = function(){
            
            if(!angular.isDefined($scope.data.entidade.requisitoCursoBloqueio ) || $scope.data.entidade.requisitoCursoBloqueio==null || $scope.data.entidade.requisitoCursoBloqueio=="") {
                showMsg(growl, "Requisito Curso Bloqueio é obrigatório.");
                return false;
            }
            if((!angular.isDefined($scope.data.entidade.identificacaoRecolhimentoCnh ) || $scope.data.entidade.identificacaoRecolhimentoCnh==null || $scope.data.entidade.identificacaoRecolhimentoCnh=="") && $scope.data.entidade.parteProcessoJuridico == 'CONDUTOR') {
                showMsg(growl, "Identificação Recolhimento CNH é obrigatória.");
                return false;
            }
            if((!angular.isDefined($scope.data.tipoProcesso) || $scope.data.tipoProcesso==null || $scope.data.tipoProcesso == "") && !$scope.desabilitaTipoProcesso) {
                showMsg(growl, "Tipo Processo é obrigatório.");
                return false;
            }
            return true;
        }
        
        $scope.buscarProcessos = function (cpf) {
            if (detranUtil.ehBrancoOuNulo(cpf))
                return;
            
            $scope.carregar(config.urls.carregarprocessos, {cpf: cpf}).then(function (retorno) {
                if (retorno.error != "") {
                    detranUtil.showMsg(growl, retorno.error);
                } else if (retorno.warning != "" && angular.isDefined(retorno.warning)) {
                    detranUtil.showMsgWarn(growl, retorno.warning);
                } else {
                    $scope.data.cpf = angular.copy(cpf);
                    $scope.data.entidade.unidadePenal = 'DIA';
                    $scope.data.isCondutor = retorno.objectResponse.isCondutor;
                    if($scope.data.isCondutor){
                        $scope.data.entidade.parteProcessoJuridico = 'CONDUTOR';
                    }
                    populaTabelaProcessosParaProcessoJuridico(retorno.objectResponse.processosAdministrativosParaProcessoJuridico);
                    $scope.data.numeroDetran = angular.copy(retorno.objectResponse.numeroDetran);
                    if (angular.isDefined($scope.data.numeroDetran)) {
                        $scope.existePessoaNoSistema = true;
                    } else {
                        $scope.existePessoaNoSistema = false;
                    }
                    $scope.cnhEntregue = retorno.objectResponse.cnhEntregue;
                    $scope.dataInicioPenalidade = retorno.objectResponse.dataInicioPenalidade;
                    $scope.data.entidade.dataInicioPenalidade = retorno.objectResponse.dataInicioPenalidade;
                    $scope.bloqueiaDataInicio = angular.isDefined($scope.data.entidade.dataInicioPenalidade) && $scope.data.entidade.dataInicioPenalidade != null;
                    
                    if (!detranUtil.ehBrancoOuNulo(retorno.objectResponse.entidade) &&
                            angular.isDefined(retorno.objectResponse.entidade.identificacaoRecolhimentoCnh)) {
                        $scope.data.entidade.identificacaoRecolhimentoCnh = angular.copy(retorno.objectResponse.entidade.identificacaoRecolhimentoCnh);
                        $scope.identificacaoRecolhimentoCnh = angular.copy(retorno.objectResponse.entidade.identificacaoRecolhimentoCnh);
                    }
                    
                    if($scope.cnhEntregue){
                        $scope.data.entidade.dataEntrega = retorno.objectResponse.dataEntregaCnh;
                    }
                }
                $scope.buscaRealizada = true;
            });
        };
        
        function populaTabelaProcessosParaProcessoJuridico(listaProcessos) {
            $scope.configTabelaProcessosParaProcessoJuridico = {
                tableName: "TabelaProcessosParaProcessoJuridico",
                name: "processosTabelaProcessosParaProcessoJuridico",
                urls: {
                },
                model: [
                    {field: "entidade.id", hidden: "hide"},
                    {field: "entidade.versaoRegistro", hidden: "hide"},
                    {field: "entidade.numeroProcessoMascarado", displayName: "Número Processo"},
                    {field: "tipoLabel", displayName: "Tipo"},
                    {field: "resMotivoLabel", displayName: "Motivo"},
                    {field: "descricaoAndamento", displayName: "Andamento"},
                    {field: "penalidadeProcesso.dataInicioPenalidade", displayName: "Data Inicio Pen."},
                    {field: "dataFimPenalidadeFormatada", displayName: "Data Fim Pen."},
                    {field: "reqCursoBloqueioLabel", displayName: "Req. Curso Bloqueio"}
                ],
                //                itemsPerPage: 7,
            };

            $scope.configTabelaProcessosParaProcessoJuridico.customData = listaProcessos;
            $scope.hasDataTabelaProcessosParaProcessoJuridico = !_.isEmpty(listaProcessos);
        };
        
        $scope.buscarOrgaoJudicial = function() {
            try {
                
                $scope.orgaoJudicialSelect = [];
                var tribunal = utils.convertSelectItemToObject($scope.data.tribunal);
                var params = {
                    tribunal: {
                        id: tribunal.id,
                        versaoRegistro: tribunal.versaoRegistro
                    }
                };

                srvRest.pesquisar(config.urls.buscarOrgaoJudicial, params).then(function (retorno) {
                    if (!_.isEmpty(retorno.error)) {
                        showMsg(growl, retorno.error);
                        return;
                    } else {
                        if (angular.isDefined(retorno.objectResponse)) {
                            for (var a = 0; a < retorno.objectResponse.length; a++) {
                                $scope.orgaoJudicialSelect.push({value: retorno.objectResponse[a].id, 
                                                                 label: retorno.objectResponse[a].descricao, 
                                                                 object: angular.toJson(retorno.objectResponse[a])
                                                                });
                            }
                        }
                    }
                });
                
            } catch (e) {
                
            }
        };
        
        $scope.prazoIndetermindadoProcessoJuridicoChanged = function () {
            $scope.data.entidade.prazoPenalidade = null;
        };
        
        $scope.identificacaoRecolhimentoCnhPJUChanged = function () {
            $scope.data.entidade.dataInicioPenalidade = $scope.dataInicioPenalidade;
            $scope.data.entidade.dataEntrega = null;
        };
        
        /**
         * Limpar renach na mudança da parte processo juridico.
         */
        $scope.parteProcessoJuridicoChanged = function() {
            
            if(!$scope.cnhEntregue){
                $scope.data.entidade.dataEntrega = "";
            }

            if (angular.isDefined($scope.data.entidade)
                    && angular.isDefined($scope.data.entidade.parteProcessoJuridico)) {
                $scope.data.entidade.formularioRenach = null;
            }
            if($scope.data.entidade.parteProcessoJuridico != 'CONDUTOR'){
                $scope.data.entidade.identificacaoRecolhimentoCnh = 'INEXISTENTE';
                $scope.data.entidade.requisitoCursoBloqueio = 'SEM_CURSO';
                $scope.data.tipoProcesso = 'SUSPENSAO_JUDICIAL';
                $scope.desabilitaTipoProcesso = true;
            }else{
                $scope.cnhEntregue ? $scope.data.entidade.identificacaoRecolhimentoCnh = $scope.identificacaoRecolhimentoCnh : $scope.data.entidade.identificacaoRecolhimentoCnh = null;
                $scope.data.entidade.requisitoCursoBloqueio = null;
                $scope.data.tipoProcesso = null;
                $scope.desabilitaTipoProcesso = false;
            }
        };
        
        /**
         * Limpar e bloquear campos ao trocar cpf na tela.
         */
        $scope.cpfProcessoJudicialChanged = function() {
            
            if (angular.isDefined($scope.data)
                    && angular.isDefined($scope.data.entidade)) {
                $scope.limparCamposProcessoJudicial();
            }
        };
        
        /**
         * 
         */
        $scope.tipoDocumentoPJUChanged = function() {
            
            if (angular.isDefined($scope.data)
                    && angular.isDefined($scope.data.tipoDocumento)
                    && $scope.data.tipoDocumento != '') {
                
               $scope.data.tipoDocumento = angular.fromJson($scope.data.tipoDocumento);
                
                if ($scope.data.tipoDocumento.codigo == 0) {
                    $scope.data.entidade.valorDocumento = $scope.criteria.cpf;
                    $scope.bloquearValorDocumentoPju = true;
                } else {
                    $scope.data.entidade.valorDocumento = null;
                    $scope.bloquearValorDocumentoPju = false;
                }
                
            } else {
                $scope.data.entidade.valorDocumento = null;
                $scope.bloquearValorDocumentoPju = false;
            }
        };
        
        /**
         * Limpar os campos da tela.
         */
        $scope.limparCamposProcessoJudicial = function() {
            
            $scope.hasDataTabelaProcessosParaProcessoJuridico = false;
            
            $scope.data={tipoProcesso: null};
            $scope.data.entidade = {
                identificacaoRecolhimentoCnh: null,
                requisitoCursoBloqueio: null
            };
            
            $scope.existePessoaNoSistema = false;
            $scope.buscaRealizada = false;
            $scope.cnhEntregue = false;
            $scope.bloquearValorDocumentoPju = false;
            $scope.desabilitaTipoProcesso = false;
        };
        
        
        $scope.alteraTipoProcesso = function(){
            if(detranUtil.ehBrancoOuNulo($scope.data.entidade.requisitoCursoBloqueio)
                    || $scope.data.entidade.requisitoCursoBloqueio == 'CURSO'){
                $scope.desabilitaTipoProcesso = false;
                $scope.data.tipoProcesso = '';
                return;
            }
            if($scope.data.entidade.requisitoCursoBloqueio == 'CURSO_EXAMES'){
                $scope.data.tipoProcesso = 'CASSACAO_JUDICIAL';
            }
            if($scope.data.entidade.requisitoCursoBloqueio == 'SEM_CURSO'){
                $scope.data.tipoProcesso = 'SUSPENSAO_JUDICIAL';
            }
            $scope.desabilitaTipoProcesso = true;
        };
        
        $scope.newPessoa = function () {
                    $scope.title = "Cadastro de Pessoa";
                    var name = "modalPessoa";
                    var modalConfiguration = {
                        name: name,
                        tplUrl: config.filesPath + '/tabelaPessoa.html',
                        modalCtrl: 'tabelaPessoaModalCtrl',
                        args: {}
                    };
                    var params = {
                        funcionalidadeOrigem: 'USUARIO',
                        codigoGrupoServico: 4
                    };
                    $scope.carregar($scope.urls.novapessoa, params).then(function (retorno) {
                        $scope.listenItensPopulados = retorno.listSelectItem;
                    });
                    if (!$scope.modalIsLoading) {
                        dtnModal.modalForm(modalConfiguration, $scope);
                    }
                    $scope.$on('modalOk' + name, function (event, retorno) {

                        if (angular.isDefined(retorno.entity) && angular.isArray(retorno.entity)) {
                            var pessoa = retorno.entity[0];
                            $scope.data.cpf = pessoa.cpfCnpj.entidade.numeroDocumento;
                            $scope.data.numeroDetran = pessoa.id;
                            $scope.criteria.cpf = pessoa.cpfCnpj.entidade.numeroDocumento;
                            $scope.existePessoaNoSistema = true;
                            $scope.data.entidade.unidadePenal = 'DIA';

                        }
                    });
                };
}])

        .controller('tabelaPessoaModalCtrl', ["$scope", "$modalInstance", "growl", "srvDetranAbstractResourceRest", '$injector', 'utils',
            function ($scope, $modalInstance, growl, srvRest, $injector, utils) {

                $injector.invoke(selectCtrl, this, {$scope: $scope});


                $scope.tabelaPessoa = {};
                $scope.tabelaPessoa.title = 'Tabela Pessoa';
                $scope.tabelaPessoa.data = {};

                $scope.buscarOrgaoEmissor = function () {
                    if (!_.isUndefined($scope.tabelaPessoa.data.documentoIdentificacao))
                        var paramTipoDoc = $scope.tabelaPessoa.data.documentoIdentificacao.entidade.tipoDocumento;
                    var tipoDoc = paramTipoDoc ? angular.fromJson(paramTipoDoc) : paramTipoDoc = {};
                    srvRest.pesquisar('orgaoemissors/getOrgaoEmissorDoTipoDocumento', {codigoTipoDocumento: tipoDoc.codigo}).then(function (retorno) {
                        $scope.orgaoEmissaoSelect = [];
                        retorno.listSelectItem = retorno.listSelectItem || {};
                        var tipoDocumento = retorno.listSelectItem[0] || {};
                        if (angular.isDefined(tipoDocumento.listValue)) {
                            for (var a = 0; a < tipoDocumento.listValue.length; a++) {
                                $scope.orgaoEmissaoSelect.push({value: tipoDocumento.listValue[a].value, label: tipoDocumento.listValue[a].label, object: tipoDocumento.listValue[a].object.selected});
                            }
                        }
                    });
                }
                $scope.validarForm = function () {
                    if (_.isUndefined($scope.tabelaPessoa) || _.isUndefined($scope.tabelaPessoa.data))
                        return true;
                    if ($scope.tabelaPessoa.data.documentoIdentificacao &&
                            $scope.tabelaPessoa.data.documentoIdentificacao.entidade.orgaoEmissao &&
                            $scope.tabelaPessoa.data.nome &&
                            $scope.tabelaPessoa.data.documentoIdentificacao.entidade &&
                            $scope.tabelaPessoa.data.documentoIdentificacao.entidade.orgaoEmissao &&
                            $scope.tabelaPessoa.data.documentoIdentificacao.entidade.numeroDocumento &&
                            $scope.tabelaPessoa.data.documentoIdentificacao.entidade.estadoEmissao &&
                            $scope.tabelaPessoa.data.cpfCnpj &&
                            $scope.tabelaPessoa.data.origem )
                        return false;
                    return true;
                }

                $scope.ok = function () {
                    var dataPreSave = angular.copy($scope.tabelaPessoa.data);


                    //se as datas informadas forem invalidas mostra mensagem de erro.
                    //se CPF inválido mostrar mensagem.
                    try {
                        if ((!ehBrancoOuNulo($scope.tabelaPessoa.data.origem.entidade.dataNascimento)
                                && $scope.tabelaPessoa.data.origem.entidade.dataNascimento.length < 10)) {
                            showMsg(growl, "A data informada está inválida ou incompleta");
                            return;
                        }

                        // VERIFICA CPF
                        if (!ehBrancoOuNulo($scope.tabelaPessoa.data.cpfCnpj)) {
                            if (!validarCpf($scope.tabelaPessoa.data.cpfCnpj.entidade.numeroDocumento)) {
                                showMsgWarn(growl, 'CPF inválido. Favor digitar o CPF completo!');
                                return;
                            }
                        }

                    } catch (e) {
                        console.log(e);
                    }

                    $scope.tabelaPessoa.data.funcionalidadeOrigem = 'PESSOA_FISICA_CONDUTOR_PROCESSO_ADMINISTRATIVO';
                    $scope.tabelaPessoa.data.codigoGrupoServico = 4;
                    $scope.tabelaPessoa.data.nomeComponente = 'btnSave';

                    if (angular.isDefined($scope.tabelaPessoa.data.documentoIdentificacao)) {

                        $scope.tabelaPessoa.data.documentoIdentificacao.entidade.orgaoEmissao =
                                utils.convertSelectItemToObject(
                                        $scope.tabelaPessoa.data.documentoIdentificacao.entidade.orgaoEmissao);

                        $scope.tabelaPessoa.data.documentoIdentificacao.entidade.estadoEmissao =
                                utils.convertSelectItemToObject(
                                        $scope.tabelaPessoa.data.documentoIdentificacao.entidade.estadoEmissao);

                        $scope.tabelaPessoa.data.documentoIdentificacao.entidade.tipoDocumento =
                                utils.convertSelectItemToObject(
                                        $scope.tabelaPessoa.data.documentoIdentificacao.entidade.tipoDocumento);

                        //
                        //                        $scope.tabelaPessoa.data.documentoIdentificacao.entidade.tipoDocumento = {codigo: angular.fromJson($scope.tabelaPessoa.data.documentoIdentificacao.entidade.tipoDocumento).codigo,
                        //                            id: angular.fromJson($scope.tabelaPessoa.data.documentoIdentificacao.entidade.tipoDocumento).id};
                    }

                    if (angular.isDefined($scope.tabelaPessoa.data.cpfCnpj)) {
                        $scope.tabelaPessoa.data.cpfCnpj.entidade.tipoDocumento = {codigo: 0};
                    }

                    if (angular.isDefined($scope.tabelaPessoa.data.documentoIdentificacao) &&
                            angular.isDefined($scope.tabelaPessoa.data.documentoIdentificacao.numeroDocumento) &&
                            $scope.tabelaPessoa.data.documentoIdentificacao.numeroDocumento === '') {

                        $scope.tabelaPessoa.data.documentoIdentificacao.numeroDocumento = null;
                    }

                    if (angular.isDefined($scope.tabelaPessoa.data.contato) &&
                            $scope.tabelaPessoa.data.contato.telefone === '') {

                        $scope.tabelaPessoa.data.contato.telefone = null;
                    }

                    srvRest.gravar('pessoafisicas/save', $scope.tabelaPessoa.data).then(function (retorno) {
                        var erro = false;
                        if (angular.isDefined(retorno.error) && angular.isArray(retorno.error) && retorno.error.length > 0) {
                            erro = true;
                            angular.forEach(retorno.error, function (msgError) {
                                showMsg(growl, msgError);
                            });

                            if (erro) {
                                $scope.tabelaPessoa = {data: angular.copy(dataPreSave)};
                            }
                        } else {
                            //salvou
                            showMsgSuccess(growl, retorno.message);
                            $modalInstance.close(retorno);
                        }
                    }, function (err) {
                        showMsg(growl, err.error);
                    });
                };

                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };
            }])
        ;
