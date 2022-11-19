angular.module('processoadministrativoinfracao', ['ngRoute'])

.constant("processoadministrativoinfracaoConfig", {
    name: 'processoadministrativoinfracao',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/processoadministrativoinfracaos/search'
    },
    model: [
        {field:"id", hidden: "hide"},
        {field:"entidade.autoInfracao", displayName:"processoadministrativoinfracao.label.autoInfracao" },
        {field:"entidade.codigoInfracao", displayName:"processoadministrativoinfracao.label.codigoInfracao" },
        {field:"entidade.dataInfracao", displayName:"processoadministrativoinfracao.label.dataInfracao" },
        {field:"orgaoAutuadorLabel", displayName:"processoadministrativoinfracao.label.orgaoAutuador" },
        {field:"origemInfracaoLabel", displayName:"processoadministrativoinfracao.label.origemInfracao" },
        {field:"entidade.quantidadePontosInfracao", displayName:"processoadministrativoinfracao.label.quantidadePontosInfracao" },
        {field:"entidade.placa", displayName:"processoadministrativoinfracao.label.placa" },
        {field: "actions", actions: [
          {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"}
        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativoinfracao',
    urlPathMestre: '/adm/processoadministrativo',
    hasMestre: true,
    i18nextResources:{
        "processoadministrativoinfracao":{
            "titulo":"Infrações",
            "label":{
                situacao: "Situação",
                isn: "ISN",
                autoInfracao: "Auto Infração",
                codigoInfracao: "Código Infração",
                dataInfracao: "Data/Hora Infração",
                orgaoAutuador: "Órgão Autuador",
                origemInfracao: "Origem Infração",
                quantidadePontosInfracao: "Quantidade Pontos",
                placa: "Placa",
                local: "Local Infração",
                origemInformacaoPontuacao: "Origem Informação Pontuação",
                situacaoPontuacao: "Situação Pontuação",
                statusPontuacao: "Status Pontuação",
                artigo: "Artigo",
                infracao: "Descrição Infração"
            }
        }
    }
})

.controller('processoadministrativoinfracaoCtrl', ['$scope', '$injector', 'processoadministrativoinfracaoConfig', 'srvDetranAbstractResourceRest', '$rootScope', 'srvTable',
    function ($scope, $injector, config, srvRest, $rootScope, srvTable) {
        $injector.invoke(ctrlFiltroDtl, this, {$scope: $scope, $rootScope: $rootScope, config: config});
        $scope.showTabs = false;
        
        $scope.hasData = false;
        
        $scope.$on('tabClicked', function(event, tab){
            if (tab != 'processoadministrativoinfracao') return;
            
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
            
            $scope.$on('aposPesquisarNoDetalhe' + config.name, function(event, obj){
                  $scope.total = obj.retorno.objectResponse;
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