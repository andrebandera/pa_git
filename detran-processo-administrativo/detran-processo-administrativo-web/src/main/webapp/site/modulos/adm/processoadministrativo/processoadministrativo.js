angular.module('processoadministrativo', ['ngRoute'])

.constant("processoadministrativoConfig", {
    name: 'processoadministrativo',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/processoadministrativos/search',
        "exportar": 'detran-processo-administrativo/resource/processoadministrativos/exportar',
        "emitir": 'detran-processo-administrativo/resource/processoadministrativos/emitir',
        "integrarinfoprova": 'detran-processo-administrativo/resource/controlecnhpas/integrarinfoprova'
    },
    model: [
        {field: "id", hidden: "hide"},
        {field: "entidade.cpf", displayName: "processoadministrativo.label.cpf"},
        {field: "entidade.cnh", displayName: "processoadministrativo.label.cnh"},
        {field: "tipoLabel", displayName: "processoadministrativo.label.tipo"},
        {field: "entidade.numeroProcessoMascarado", displayName: "processoadministrativo.label.numeroProcesso"},
        {field: "numeroPortariaMascarado", displayName: "processoadministrativo.label.numeroPortaria"},
        {field: "descricaoAndamento", displayName: "processoadministrativo.label.descricaoAndamento"},
//        {field: "entidade.dataProcessamento", displayName: "processoadministrativo.label.dataProcessamento"},
        {field: "penalidadeProcesso.dataInicioPenalidade", displayName: "processoadministrativo.label.dataInicioPenalidade"},
        {field: "penalidadeProcesso.dataFimPenalidade", displayName: "processoadministrativo.label.dataFimPenalidade"},
//        {field: "origemLabel", displayName: "processoadministrativo.label.origem"},
        {field: "actions", actions: [
            {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"},
            {name: "exportar", icon: "global.btn.download.icon", btn: "global.btn.download.btn", title: "Consulta Completa"}
        ]}
    ],
    btnFiltro: ['pesquisar', 'limpar'],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/processoadministrativo',
    urlPath: '/adm/processoadministrativo',
    i18nextResources: {
        "processoadministrativo":{
            "titulo":"Processo Administrativo", 
            "label": {
                cpf: "CPF",
                cnh: "CNH",
                tipo: "Tipo Processo",
                numeroDetran: "Número Detran",
                numeroProcesso: "Número Processo",
                numeroPortaria: "Número Portaria",
                descricaoAndamento: "Andamento",
                dataProcessamento: "Data Instauração",
                andamento: "Andamento",
                ativo: "Situação",
                nomeCondutor: "Nome Condutor",
                numeroRegistro: "Número Registro",
                motivo: "Motivo",
                fluxoProcessoDescricao: "Fluxo Processo",
                fase: "Fase Processo",
                situacaoAndamento: "Situação Andamento",
                dataInicioPenalidade: "Início Penalidade",
                dataFimPenalidade: "Fim Penalidade",
                tempoPenalidade: "Tempo Penalidade",
                dataProva: "Data Prova",
                notaProva: "Nota Prova",
                local: "Local Prova",
                cenario: "Cenário",
                origem: "Origem",
                numeroContato: "Número Contato",
                emailContato: "E-mail Contato"
            }
        }
    }
})

.config(["$routeProvider", "processoadministrativoConfig", function ($rp, config) {
    var path = config.filesPath;
    var urlPath = config.urlPath;
    var name = config.name;
    
    $rp
    .when( urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'processoadministrativo.titulo'})
    .when( urlPath + '/novo', {templateUrl: path + '/form.html', controller: name + 'Create', label: 'global.label.breadcrumb.novo'})
    .when( urlPath + '/ver', {templateUrl: path + '/form.html', controller: name + 'View', label: 'global.label.breadcrumb.ver'});
}])

.controller('processoadministrativoCtrl', ["$scope", "$injector", "processoadministrativoConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('processoadministrativoList', ['$scope', '$controller', '$injector', 'processoadministrativoConfig', "srvDetranAbstractResourceRest", "growl", "$rootScope", "dtnAttachFiles",
    function($scope, $controller, $injector, config, srvRest, growl, $rootScope, dtnAttachFiles){
        $controller('processoadministrativoCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        $scope.printBtns = [{name: 'PDF', color: 'red'}];
        $scope.desabilitaImpressao = true;

        $scope.load();
        
        $scope.showList = false;
        $scope.showTabJudicial = false;
        
        $scope.$on("limparfiltros" + config.name, function () {
            $scope.showList = false;
            $scope.desabilitaImpressao = true;
         });
     
        $scope.$on("pesquisarfiltros" + config.name, function(event) {
            $scope.showList = true;
        });
        
        $scope.$on('afterLoadDataBasicCtrl' + config.name, function(event, obj) {
            $scope.total = $scope.pagination.totalItems;
            if($scope.dataForTable.length > 0){
                $scope.desabilitaImpressao = false;
                $scope.showList = true;
            }
            else{
                $scope.desabilitaImpressao  = true;
                $scope.showList = false;
            }
        });
        
                $scope.$on('getAction' + config.name, function (e, linha) {
            var id = _.find(linha.data, {field: "id"}).value;
            
            if (linha.action === "exportar") {
                
                exportar(id);
                
            } 
        });
             
        function exportar(id){
            $scope.editar(config.urls.exportar, {id:id}).then(function (retorno) {

                if (!_.isEmpty(retorno.error)) {

                    detranUtil.showMsg(growl,retorno.error);

                } else {

                    var byteArquivo = retorno.objectResponse;

                    if (detranUtil.ehBrancoOuNulo(byteArquivo)) {

                        detranUtil.showMsg(growl, "Arquivo inválido.");
                        $rootScope.loading = false;
                        return
                    }

                    var item = { 
                        byteArquivo : byteArquivo,
                        extensao : "PDF",
                        usePdfJS : false,
                        nameModal : "Consulta Completa Processo Administrativo",
                        orientation : 'landscape'
                    };

                    dtnAttachFiles().openFile(item, $scope);
                }
            }, function (err) {
                detranUtil.showMsg(growl,err.error);
            });
        }
        
        
        $scope.emitir = function (type) {
            
            var filtros = angular.copy($scope.filtros.data);
            
            if (filtros.tipo == "")
                delete(filtros.tipo);
            
            if (!detranUtil.ehBrancoOuNulo(filtros.andamento)) {
                var andamentoAux = angular.copy(angular.fromJson(filtros.andamento));
                filtros.andamento = angular.fromJson(andamentoAux);
            }else{
                filtros.andamento = null;
            }
            if (!detranUtil.ehBrancoOuNulo(filtros.cenario)) {
                var cenarioAux = angular.copy(filtros.cenario);
                filtros.cenario = angular.fromJson(cenarioAux);
            }else{
                filtros.cenario = null;
            }
            
            srvRest.editar(config.urls.emitir, filtros).then(function (retorno) {
                if (!_.isEmpty(retorno.error)) {
                    growl.addErrorMessage(retorno.error);
                } else {
                    
                    var newWindow = true;
                    
                    var item = {
                        params: {},
                        labelDoc: "Relatório Processo Administrativo",
                        descricao: "",
                        ativaDescricao: false,
                        typeApplication: "application/pdf",
                        byteArquivo: retorno.entity[0].relatorio || ""
                    };
                    
                    item.extensao = "PDF";
                    item.params.excluir = false;
                    
                    $scope.files = new dtnAttachFiles();
                    $scope.config.attachModalPathFile = $rootScope.urlPartials + 'modais/generico/modalArquivo.html';
                    $scope.files.setConfig($scope.config);
                    $scope.tituloContinuar  = "Fechar";                    
                    $scope.files.openFile(item, $scope, newWindow, "rel_processos_administrativos");
                }
            }, function (err) {
                growl.addErrorMessage(err.error);
            });
        };
    }
])

.controller('processoadministrativoCreate', ['$scope', '$parse', '$controller',
    function($scope, $parse, $controller){
        $controller('processoadministrativoCtrl', {$scope: $scope});     
        
        $scope.page = 'create';
        
        $scope.preSave = function() {
            $scope.save();
        };
    }
])

.controller('processoadministrativoView', ["$scope", "$controller", '$rootScope', 'srvMestreDtl', 'growl', '$location', 'processoadministrativoConfig',
    function($scope, $controller, $rootScope, srvMestreDtl, growl, $location, config){
        
        $scope.page = 'view';
        $controller('processoadministrativoCtrl', {$scope: $scope});
        
                carregarDados();

        function carregarDados (){
            $scope.editar($scope.urls.pesquisar, '{"id": '+ $rootScope.idPaginaAtual +'}').then(function(retorno){
                if (!_.isEmpty(retorno.error)) {
                        detranUtil.showMsg(growl,retorno.error);
                        $location.path(config.urlPath);
                } else {

                    $scope.data = retorno.entity[0];
                    $rootScope.mestre.processoadministrativo = srvMestreDtl.setMestre('processoAdministrativo', $scope.data.entidade);

                    $scope.cabecalho ={
                                data: [
                                        {label: 'Número Processo', value: $scope.data.entidade.numeroProcessoMascarado, cols: "6 col-md-3"},
                                        {label: 'CPF', value: $scope.data.entidade.cpf, cols: "6 col-md-3"},
                                        {label: 'Data Processo', value: $scope.data.entidade.dataProcessamento, cols: "6 col-md-3"},
                                        {label: 'Informações Desistência', value: $scope.data.desistenteLabel, cols: "6 col-md-3"}
                                    ],
                                start : function(){
                                    detranUtil.fnBar().start();                                    
                                }
                            };
                    $scope.showTabJudicial = $scope.data.entidade.origem == 'JURIDICA';
                }
                
                $scope.configTableProcessosFilho = {
                    tableName: "TableProcessosFilho",
                    name: "processosFilhoTable",
                    urls: {
                    },
                    model: [
                        {field: "id", hidden: "hide"},
                        {field: "versaoRegistro", hidden: "hide"},
                        {field: "numeroProcessoMascarado", displayName: "Número Processo"},
                        {field: "tipo", displayName: "Tipo de Processo"}
                    ]
                    //                itemsPerPage: 7,
                };

                $scope.configTableProcessosFilho.customData = $scope.data.processosFilho;
                $scope.hasData = !_.isEmpty($scope.data.processosFilho);    
            });
        }
        
        $scope.atualizarInformacoesProva = function(){
            $scope.carregar(config.urls.integrarinfoprova, $scope.data.entidade.numeroProcessoMascarado).then(function(retorno){
                carregarDados();
            });
        }
    }
]);
