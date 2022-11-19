angular.module('processoadministrativobloqueio', ['ngRoute'])

.constant("processoadministrativobloqueioConfig", {
    name: 'processoadministrativobloqueio',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/pa/bloqueiobca/search'
    },
    model: [
        {field:"id", hidden: "hide"},
        {field:"dataInicio", displayName:"processoadministrativobloqueio.label.dataInicio" },
        {field:"dataFim", displayName:"processoadministrativobloqueio.label.dataFim" },
        {field:"motivoBloqueioLabel", displayName:"processoadministrativobloqueio.label.motivoBloqueio" },
        {field:"motivoDesbloqueioLabel", displayName:"processoadministrativobloqueio.label.motivoDesbloqueio" },
        {field:"situacao", displayName:"processoadministrativobloqueio.label.situacao" },
        {field:"ativoLabel", displayName:"processoadministrativobloqueio.label.ativo" }
//        {field: "actions", actions: [
//          {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"}
//        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativobloqueio',
    urlPathMestre: '/adm/processoadministrativo',
    hasMestre: true,
    i18nextResources:{
        "processoadministrativobloqueio":{
            "titulo":"Bloqueio",
            "label":{
                dataInicio: "Data Início",
                situacao: "Situação",
                ativo: "Ativo",
                dataFim: "Data Fim",
                motivoBloqueio: "Motivo Bloqueio",
                motivoDesbloqueio: "Motivo Desbloqueio"
                
            }
        }
    }
})

.controller('processoadministrativobloqueioCtrl', ['$scope', '$injector', 'processoadministrativobloqueioConfig', 'srvDetranAbstractResourceRest', '$rootScope', 'srvTable',
    function ($scope, $injector, config, srvRest, $rootScope, srvTable) {
        $injector.invoke(ctrlFiltroDtl, this, {$scope: $scope, $rootScope: $rootScope, config: config});
        $scope.showTabs = false;
        
        $scope.hasData = false;
        
        $scope.$on('tabClicked', function(event, tab){
            if (tab != 'processoadministrativobloqueio') return;
            
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
            
            var actionButtons = ['ver'];
            $scope.actionButtons = srvTable.actionButtons(actionButtons);
            
            $scope.$on('doSearch', function() {
                $scope.showTabs = false;
                $scope.clear();

                $scope.$broadcast('updateData', $rootScope.mestre.processoadministrativo, true, config.name);
            });

            $scope.$on('getAction', function(event, data) { 
                if (data.action == 'ver') {
                    $scope.showTabs = true;
                    $scope.resetActions();
                    $scope.setAction('view', true);
                    
                    var params = {}
                    params.id = data.data[0].value;
                    
                    srvRest.pesquisar(config.urls.pesquisar, params).then(function(retorno) {
                        if (retorno.error != '') {
                            $scope.growl.addErrorMessage(retorno.error);
                            return;
                        }
                        $scope.data = retorno.entity[0];
                        $rootScope.loading = false;
                        $scope.$broadcast('dataIsLoaded');
                    }, function(err) {
                        $scope.growl.addErrorMessage(err.error);
                    });
                } else {
                    $scope.action(data.action, data.data);
                }
            });
        };
    }
]);