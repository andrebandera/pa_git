angular.module('consultamovimentacaopa', ['ngRoute'])

.constant("consultamovimentacaopaConfig", {
    name: 'consultamovimentacaopa',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/pa/movimentacaopa/search2',
        "emitir": 'detran-processo-administrativo/resource/pa/movimentacaopa/emitir'
    },
    model: [
        {field: "id", hidden: "hide"},
        {field: "entidade.processoAdministrativo.numeroProcessoMascarado", displayName: "consultamovimentacaopa.label.numeroProcesso"},
        {field: "tipoLabel", displayName: "consultamovimentacaopa.label.tipo"},
        {field: "entidade.movimentacaoAcao", displayName: "consultamovimentacaopa.label.acao"},
        {field: "entidade.dataCadastro", displayName: "consultamovimentacaopa.label.dataCadastro"},
        {field: "usuarioMovimentacao", displayName: "consultamovimentacaopa.label.usuario"}
    ],
    btnFiltro: ['pesquisar', 'limpar'],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/consultamovimentacaopa',
    urlPath: '/adm/consultamovimentacaopa',
    i18nextResources: {
        "consultamovimentacaopa":{
            "titulo":"Processo Administrativo", 
            "label": {
                tipo: "Tipo Processo",
                numeroProcesso: "Número Processo",
                dataCadastro: "Data Cadastro",
                usuario: "Usuário",
                dataInicio: "Data Início",
                dataFim: "Data Fim",
                acao: "Ação"
            }
        }
    }
})

.config(["$routeProvider", "consultamovimentacaopaConfig", function ($rp, config) {
    var path = config.filesPath;
    var urlPath = config.urlPath;
    var name = config.name;
    
    $rp
    .when( urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'consultamovimentacaopa.titulo'});
}])

.controller('consultamovimentacaopaCtrl', ["$scope", "$injector", "consultamovimentacaopaConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('consultamovimentacaopaList', ['$scope', '$controller', '$injector', 'consultamovimentacaopaConfig', "srvDetranAbstractResourceRest", "growl", "$rootScope", "dtnAttachFiles",
    function($scope, $controller, $injector, config, srvRest, growl, $rootScope, dtnAttachFiles){
        $controller('consultamovimentacaopaCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        
        $scope.printBtns = [{name: 'PDF', color: 'red'}];
        $scope.desabilitaImpressao = true;

        $scope.load();
        
        $scope.showList = false;
        
        $scope.$on("limparfiltros" + config.name, function () {
            $scope.showList = false;
            $scope.desabilitaImpressao = true;
         });
     
        $scope.$on("pesquisarfiltros" + config.name, function(event) {
            $scope.showList = true;
        });
        
        $scope.$on('afterLoadDataBasicCtrl' + config.name, function(event, obj) {
            $scope.total = $scope.pagination.totalItems;
            if($scope.dataForTable.length > 0)
                $scope.desabilitaImpressao = false;
            else
                $scope.desabilitaImpressao  = true;
        });
        
        $scope.emitir = function (type) {
            
            var filtros = angular.copy($scope.filtros.data);
            
            if (filtros.acao == "")
                delete(filtros.acao);
            
            srvRest.editar(config.urls.emitir, filtros).then(function (retorno) {
                if (!_.isEmpty(retorno.error)) {
                    growl.addErrorMessage(retorno.error);
                } else {
                    
                    var newWindow = true;
                    
                    var item = {
                        params: {},
                        labelDoc: "Relatório Movimentações",
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
                    $scope.files.openFile(item, $scope, newWindow, "rel_movimentacoes_pa");
                }
            }, function (err) {
                growl.addErrorMessage(err.error);
            });
        };
    }
]);