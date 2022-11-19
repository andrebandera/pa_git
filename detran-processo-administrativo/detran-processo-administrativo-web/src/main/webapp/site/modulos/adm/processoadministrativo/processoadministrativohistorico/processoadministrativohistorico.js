angular.module('processoadministrativohistorico', ['ngRoute'])

.constant("processoadministrativohistoricoConfig", {
    name: 'processoadministrativohistorico',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/processoadministrativohistoricos/search'
    },
    model: [
        {field:"id", hidden: "hide"},
        {field:"codigoAndamento", displayName:"Código Andamento" },
        {field:"andamento", displayName:"Andamento" },
        {field:"situacao", displayName:"Situação" },
        {field:"dataInicio", displayName:"Data Inicio" },
        {field:"dataTermino", displayName:"Data Término" },
        {field:"fluxo", displayName:"Fluxo" },
        {field:"fase", displayName:"Fase" }
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativohistorico',
    urlPathMestre: '/adm/processoadministrativo',
    itemsPerPage:20,
    hasMestre: true
})

.controller('processoadministrativohistoricoCtrl', ['$scope', '$injector', 'processoadministrativohistoricoConfig', 'srvDetranAbstractResourceRest', '$rootScope', 'srvTable',
    function ($scope, $injector, config, srvRest, $rootScope, srvTable) {
        $injector.invoke(ctrlFiltroDtl, this, {$scope: $scope, $rootScope: $rootScope, config: config});
        $scope.showTabs = false;
        
        $scope.hasData = false;
        
        $scope.$on('tabClicked', function(event, tab){
            if (tab != 'processoadministrativohistorico') return;
            
            if (!$scope.hasData) {
              $scope.start();
              $scope.hasData = true;
              
            } else {
                $scope.clear();
                $scope.clear();
                $scope.$broadcast('updateData', $rootScope.mestre.processoadministrativo, true, config.name);
            }
        });

        $scope.start = function() {
            var mestreID = angular.copy($rootScope.mestre.processoadministrativo);
            
            if (mestreID == undefined || mestreID == '') return;
            
            $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
            $injector.invoke(mestreDetalheBasicCtrl, this, {$scope: $scope, config: config});
        
            $scope.urlPath = $scope.$parent.urlPath;
            $scope.dataModal = {};
            
            $scope.mestreID = mestreID;
            $scope.config = config;
            
            $scope.$on('doSearch', function() {
                $scope.showTabs = false;
                $scope.clear();
                
                $scope.$broadcast('updateData', $rootScope.mestre.processoadministrativo, true, config.name);
            });
            
        };
    }
]);