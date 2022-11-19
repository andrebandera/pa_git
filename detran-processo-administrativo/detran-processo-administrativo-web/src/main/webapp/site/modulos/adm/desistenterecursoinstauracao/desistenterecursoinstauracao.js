angular.module('desistenterecursoinstauracao', ['ngRoute'])

.constant("desistenterecursoinstauracaoConfig", {
    name: 'desistenterecursoinstauracao',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/desistenterecursoinstauracaos/search',
        "desistir": 'detran-processo-administrativo/resource/desistenterecursoinstauracaos/desistir',
        "exportarprotocolo": 'detran-processo-administrativo/resource/desistenterecursoinstauracaos/exportarprotocolo',
    },
    model: [
        {field: "entidade.processoAdministrativo.id", hidden: "hide"},
        {field: "entidade.processoAdministrativo.numeroProcessoMascarado", displayName: "desistenterecursoinstauracao.label.numeroProcesso"},
        {field: "andamentoLabel", displayName: "desistenterecursoinstauracao.label.andamento"},
        {field: "temRecursoLabel", displayName: "desistenterecursoinstauracao.label.recurso"},
        {field: "desistenteLabel", displayName: "desistenterecursoinstauracao.label.desistente"},
        {field: "desistente", hidden: "hide"},
        {field: "actions", actions: [
            {name: "desistir", icon: "global.btn.excluir.icon", btn:"global.btn.excluir.btn", title:"Desistir de Recurso Instauração/Penalização"},
            {name: "exportar", icon: "global.btn.download.icon", btn: "warning", title:"Imprimir Protocolo Desistência"}
          
        ]}
    ],
    btnFiltro: ['pesquisar', 'limpar'],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/desistenterecursoinstauracao',
    urlPath: '/adm/desistenterecursoinstauracao',
    i18nextResources: {
        "desistenterecursoinstauracao":{
            "titulo":"Desistência Recurso Instauração", 
            "label": {
                cpf: "CPF Condutor",
                numeroProcesso: "Número Processo",
                andamento: "Andamento",
                recurso: "Recurso em Análise",
                dataInicio: "Data Início",
                dataFim: "Data Fim",
                desistente: "Processo Desistente",
                acao: "Ação"
            }
        }
    },
    limitSizePagination : true
})

.config(["$routeProvider", "desistenterecursoinstauracaoConfig", function ($rp, config) {
    var path = config.filesPath;
    var urlPath = config.urlPath;
    var name = config.name;
    
    $rp
    .when( urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'desistenterecursoinstauracao.titulo'});
}])

.controller('desistenterecursoinstauracaoCtrl', ["$scope", "$injector", "desistenterecursoinstauracaoConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('desistenterecursoinstauracaoList', ['$scope', '$controller', '$injector', 'desistenterecursoinstauracaoConfig', "srvDetranAbstractResourceRest", "growl", "$rootScope", "dtnAttachFiles",
    function($scope, $controller, $injector, config, srvRest, growl, $rootScope, dtnAttachFiles){
        $controller('desistenterecursoinstauracaoCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
//        $scope.load();
        
        $scope.showList = false;
        
        $scope.$on("limparfiltros" + config.name, function () {
            $scope.showList = false;
         });
     
        $scope.$on("pesquisarfiltros" + config.name, function(event) {
            $scope.showList = true;
        });
        
        $scope.$on('getAction' + config.name, function (e, linha) {
            var id = _.find(linha.data, {field: "entidade.processoAdministrativo.id"}).value;

            if (linha.action === "desistir") {

                desistir(id);

            } else if (linha.action === "exportar") {
                
                exportar(id);
                 
            }
        });
        
       $scope.rulesToDisableButton = function (btn) {

            var button = btn.button;
            var desistente = _.find(btn.linha, {'field': 'desistente'}).value;
            if (button.name == 'desistir' && desistente) {
                return true;

            }
            if (button.name == 'exportar' && !desistente) {
                return true;

            }
            return false;
        };
       
        function desistir(id) {
            
            
            srvRest.editar(config.urls.desistir, {id: id}).then(function (retorno) {
                if (!_.isEmpty(retorno.error)) {
                    growl.addErrorMessage(retorno.error);
                } else {
                    
                    showMsgSuccess(growl,retorno.message);
                    $scope.load();
                
                }
            }, function (err) {
                growl.addErrorMessage(err.error);
            });
        };
        
        function exportar(id){
            $scope.editar(config.urls.exportarprotocolo, id).then(function (retorno) {

                if (!_.isEmpty(retorno.error)) {

                    detranUtil.showMsg(growl,retorno.error);

                } else {

                    var byteArquivo = retorno.objectResponse.byteArquivo;

                    if (detranUtil.ehBrancoOuNulo(byteArquivo)) {

                        detranUtil.showMsg(growl, "Arquivo inválido.");
                        $rootScope.loading = false;
                        return;
                    }

                    var item = { 
                        byteArquivo : byteArquivo,
                        extensao : "PDF",
                        usePdfJS : false,
                        nameModal : "Protocolo Desistência Recurso Instauracao e Penalização",
                        orientation : 'landscape'
                    };

                    dtnAttachFiles().openFile(item, $scope);
                }
            }, function (err) {
                detranUtil.showMsg(growl,err.error);
            });
        };
    }
]);