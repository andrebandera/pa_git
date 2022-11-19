angular.module('processoadministrativonotificacao', ['ngRoute'])

.constant("processoadministrativonotificacaoConfig", {
    name: 'processoadministrativonotificacao',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/pa/notificacao/search'
    },
    model: [
        {field:"id", hidden: "hide"},
        {field: "entidade.numeroNotificacaoMascarado", displayName: "processoadministrativonotificacao.label.numeroNotificacao"}, 
        {field: "entidade.dataNotificacao", displayName: "processoadministrativonotificacao.label.dataNotificacao"}, 
        {field: "entidade.tipoNotificacaoProcesso", displayName: "processoadministrativonotificacao.label.tipoNotificacaoProcesso"}, 
        {field:"entidade.ativoLabel", displayName:"modulo.global.label.ativoLabel" },	
        {field: "actions", actions: [
          {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"}
        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativonotificacao',
    urlPathMestre: '/adm/processoadministrativo',
    hasMestre: true,
    i18nextResources:{
        "processoadministrativonotificacao":{
            "titulo":"Notificação",
            "label":{
                numeroNotificacao:"Número",
                dataNotificacao: "Data Notificação",
                tipoNotificacaoProcesso: "Tipo",
                objetoCorreios: "Objeto Correios AR",
                dataRetornoAr: "Data Processamento Retorno AR",
                dataTerminoPrazo: "Data Término Prazo",
                dataChegada: "Data Recebimento Correspondência",
                motivoRetorno: "Motivo Retorno",
                dataPortaria: "Data Portaria",
                numeroPortaria: "Número Portaria",
                tempoPenalidade: "Tempo Penalidade (meses)",
                dataPublicacao: "Data Publicação",
                numeroEdital: "Número Edital",
                nomeRecebedorAr: "Nome Recebedor AR",
                tipoEndereco: "Tipo",
                rua: "Rua",
                numeroEndereco: "Nº",
                bairro: "Bairro",
                complemento: "Complemento",
                municipio: "Município",
                uf: "UF",
                cep: "CEP",
                documentoRecebedorAr: "Documento Recebedor AR",
                prazoEdital: "Prazo Edital"
            }
        }
    }
})

.controller('processoadministrativonotificacaoCtrl', ['$scope', '$injector', 'processoadministrativonotificacaoConfig', 'srvDetranAbstractResourceRest', '$rootScope', 'srvTable',
    function ($scope, $injector, config, srvRest, $rootScope, srvTable) {
        $injector.invoke(ctrlFiltroDtl, this, {$scope: $scope, $rootScope: $rootScope, config: config});
        $scope.showTabs = false;
        
        $scope.hasData = false;
        
        $scope.$on('tabClicked', function(event, tab){
            if (tab != 'processoadministrativonotificacao') return;
            
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