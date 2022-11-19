angular.module('cnhcartorio',['ngRoute'])
    
.constant("cnhcartorioConfig",{
    name: 'cnhcartorio',
    urls: {
        "gravar":'detran-processo-administrativo/resource/cnhcartorios/save',
        "pesquisar":'detran-processo-administrativo/resource/cnhcartorios/search',
        "carregarprocessos":'detran-processo-administrativo/resource/cnhcartorios/carregarprocessos',
    },
    model:[
        {field:"entidade.id", hidden: "hide"},
        {field:"entidade.processoJudicial.isn", displayName:"cnhcartorio.label.isn" },
        {field:"entidade.ativoLabel", displayName:"modulo.global.label.ativoLabel" },	
        {field: "actions", actions: [
          {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"},
          {name: "editar", icon: "global.btn.editar.icon", btn:"global.btn.editar.btn", title:"global.btn.editar.tooltip"},
          {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title:"global.btn.desativar.tooltip"},
          {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title:"global.btn.reativar.tooltip"}
        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/cnhcartorio',
    urlPath: '/adm/cnhcartorio',
    i18nextResources : {
        "cnhcartorio" : {
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
                "msgCnhEntregue": "Atenção!",
                "dataBloqueio":"Data Bloqueio"
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

.config(["$routeProvider", "cnhcartorioConfig", function ($rp, config) {
    var path = config.filesPath;
    var urlPath = config.urlPath;
    var name = config.name;
    
    $rp
    .when( urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'cnhcartorio.titulo'});
}])

.controller('cnhcartorioCtrl', ["$scope", "$injector", "cnhcartorioConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('cnhcartorioList', ['$scope', '$controller', function($scope, $controller){
        $scope.page = 'list';
        $controller('cnhcartorioCustomCtrl', {$scope: $scope});
    }
])

.controller('cnhcartorioCustomCtrl', [
"$scope", "$controller", "cnhcartorioConfig", "$rootScope", "$injector", "$location", "srvDetranAbstractResourceRest", "utils", "growl", "detranModal",
    function($scope, ctrl, config, $rootScope, $injector, $location, srvRest, utils, growl, dtnModal){
        ctrl('cnhcartorioCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        $scope.inicializaVariaveis = function (){
            $scope.data.entidade = {};
            $scope.data.entidade.unidadePenal = 'DIA';
            $scope.hasDataTabelaProcessosParaProcessoJuridico = false;
            $scope.existePessoaNoSistema = false;
            $scope.buscaRealizada = false;
            $scope.dataModal = {};
            $scope.criteria = {};
        };
        
        $scope.inicializaVariaveis();
        
        $scope.validateForm = function(){
            return (detranUtil.ehBrancoOuNulo($scope.data)
                        || detranUtil.ehBrancoOuNulo($scope.data.entidade)
                        || detranUtil.ehBrancoOuNulo($scope.data.entidade.dataInicioPenalidade)
                        || detranUtil.ehBrancoOuNulo($scope.data.entidade.dataEntrega)
                        || detranUtil.ehBrancoOuNulo($scope.data.entidade.observacao)
                        || detranUtil.ehBrancoOuNulo($scope.data.entidade.dataBloqueio)
                    );
        }
        
        $scope.preSave = function() {            
            $scope.save();
        };
        
        $scope.$on('updateData' + config.name, function(){
            $scope.inicializaVariaveis();
        });
        
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
                    populaTabelaProcessosParaProcessoJuridico(retorno.objectResponse.processosAdministrativosParaProcessoJuridico);
                    $scope.data.processosAdministrativosParaProcessoJuridico = retorno.objectResponse.processosAdministrativosParaProcessoJuridico;
                    $scope.data.numeroDetran = angular.copy(retorno.objectResponse.numeroDetran);
                    if (angular.isDefined($scope.data.numeroDetran)) {
                        $scope.existePessoaNoSistema = true;
                    } else {
                        $scope.existePessoaNoSistema = false;
                    }
                    $scope.dataInicioPenalidade = retorno.objectResponse.dataInicioPenalidade;
                    $scope.data.entidade.dataInicioPenalidade = retorno.objectResponse.dataInicioPenalidade;
                    $scope.bloqueiaDataInicio = angular.isDefined($scope.data.entidade.dataInicioPenalidade) && $scope.data.entidade.dataInicioPenalidade != null;
                    $scope.data.entidade.identificacaoRecolhimentoCnh = 'CARTORIO_JUDICIARIO';
                    
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
                    {field: "penalidadeProcesso.dataFimPenalidade", displayName: "Data Fim Pen."},
                    {field: "reqCursoBloqueioLabel", displayName: "Req. Curso Bloqueio"}
                ],
                //                itemsPerPage: 7,
            };

            $scope.configTabelaProcessosParaProcessoJuridico.customData = listaProcessos;
            $scope.hasDataTabelaProcessosParaProcessoJuridico = !_.isEmpty(listaProcessos);
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
        
}]);