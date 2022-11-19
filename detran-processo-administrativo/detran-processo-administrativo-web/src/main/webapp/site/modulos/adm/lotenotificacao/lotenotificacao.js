angular.module('lotenotificacao', ['ngRoute'])

.constant("lotenotificacaoConfig", {
    name: 'lotenotificacao',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/lotenotificacaos/search',
        "exportar": 'detran-processo-administrativo/resource/lotenotificacaos/exportar'
    },
    model: [
        {field: "id", hidden: "hide"},
        {field: "dataGeracao", displayName: "lotenotificacao.label.dataGeracao"},
        {field: "nome", displayName: "lotenotificacao.label.nome"},
        {field: "tipoLabel", displayName: "lotenotificacao.label.tipo"},
        {field: "qtdNotificacoes", displayName: "lotenotificacao.label.quantidade"},
        {field: "actions", actions: [
            {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"},
            {name: "exportar", icon: "global.btn.download.icon", btn: "global.btn.download.btn", title: "Arquivo Lote"}
        ]}
    ],
    btnFiltro: ['pesquisar', 'limpar'],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/lotenotificacao',
    urlPath: '/adm/lotenotificacao',
    i18nextResources: {
        "lotenotificacao":{
            "titulo":"Lote Notificações",
            "label": {
                tipo: "Tipo Notificação",
                nome: "Lote",
                quantidade: "Qde Notificações",
                objetoCorreios: "Objeto Correios",
                ativo: "Situação",
                dataInicio: "Data Lote Inicio",
                dataGeracao: "Data Lote",
                dataFim: "Data Lote Fim",
                numeroProcesso: "Número Processo",
                numeroNotificacao: "Número Notificação"
            }
        }
    }
})

.config(["$routeProvider", "lotenotificacaoConfig", function ($rp, config) {
    var path        = config.filesPath;
    var urlPath     = config.urlPath;
    var name        = config.name;
    
    $rp
    .when( urlPath, {templateUrl: path + '/index.html', controller: name + 'List', label: 'lotenotificacao.titulo'})
    .when( urlPath + '/novo', {templateUrl: path + '/form.html', controller: name + 'Create', label: 'global.label.breadcrumb.novo'})
    .when( urlPath + '/ver', {templateUrl: path + '/form.html', controller: name + 'View', label: 'global.label.breadcrumb.ver'});
}])

.controller('lotenotificacaoCtrl', ["$scope", "$injector", "lotenotificacaoConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('lotenotificacaoList', ['$scope', '$controller', '$injector', 'lotenotificacaoConfig', "srvDetranAbstractResourceRest", "growl", "$rootScope", "dtnAttachFiles",
    function($scope, $controller, $injector, config, srvRest, growl, $rootScope, dtnAttachFiles){
        $controller('lotenotificacaoCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
        

        $scope.load();
        
        $scope.showList = false;

        $scope.$on("limparfiltros" + config.name, function () {
            $scope.showList = false;
         });
     
        $scope.$on("pesquisarfiltros" + config.name, function(event) {
            $scope.showList = true;
        });
        
        $scope.$on('afterLoadDataBasicCtrl' + config.name, function(event, obj) {
            $scope.total = $scope.pagination.totalItems;
            if($scope.dataForTable.length > 0){
                $scope.showList = true;
            }else{
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
    }
])

.controller('lotenotificacaoCreate', ['$scope', '$parse', '$controller',
    function($scope, $parse, $controller){
        $controller('lotenotificacaoCtrl', {$scope: $scope});
        $scope.page = 'create';
    }
])

.controller('lotenotificacaoView', ["$scope", "$controller", '$rootScope', 'srvMestreDtl', 'growl', '$location', 'lotenotificacaoConfig',
    function($scope, $controller, $rootScope, srvMestreDtl, growl, $location, config){
        
        $scope.page = 'view';
        $controller('lotenotificacaoCtrl', {$scope: $scope});

        $scope.editar($scope.urls.pesquisar, '{"id": '+ $rootScope.idPaginaAtual +'}').then(function(retorno){
            if (!_.isEmpty(retorno.error)) {
                    detranUtil.showMsg(growl,retorno.error);
                    $location.path(config.urlPath);
            } else {
                $scope.data = retorno.entity[0];
                $rootScope.mestre.lotenotificacao = srvMestreDtl.setMestre('lotenotificacao', $scope.data);
            }
        });
    }
]);
