angular.module('notificacaolote', ['ngRoute'])

.constant("notificacaoloteConfig", {
    name: 'notificacaolote',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/pa/notificacao/search'
    },
    model: [
        {field:"id", hidden: "hide"},
        {field:"entidade.dataNotificacao", displayName:"notificacaolote.label.dataNotificacao" },
        {field:"entidade.numeroNotificacaoMascarado", displayName:"notificacaolote.label.numeroNotificacao" },
        {field:"entidade.objetoCorreio", displayName:"notificacaolote.label.objetoCorreios" },
        {field:"entidade.tipoNotificacaoProcesso", displayName:"notificacaolote.label.tipoNotificacao" },
        {field:"entidade.processoAdministrativo.numeroProcessoMascarado", displayName:"notificacaolote.label.numeroProcesso" },
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/lotenotificacao/notificacaolote',
    urlPathMestre: '/adm/lotenotificacao',
    hasMestre: true,
    itensPerPage: 10,
    i18nextResources:{
        "notificacaolote":{
            "titulo":"Notificações",
            "label":{
                dataNotificacao: "Data Notificação",
                numeroNotificacao: "Número Notificação",
                objetoCorreios: "Objeto Correios",
                tipoNotificacao: "Tipo Notificação",
                numeroProcesso: "Número Processo"
            }
        }
    }
})

.controller('notificacaoloteCtrl', ['$scope', '$injector', 'notificacaoloteConfig', 'srvDetranAbstractResourceRest', '$rootScope', 'srvTable',
    function ($scope, $injector, config, srvRest, $rootScope, srvTable) {
        $injector.invoke(ctrlFiltroDtl, this, {$scope: $scope, $rootScope: $rootScope, config: config});
        $scope.showTabs = false;
        
        $scope.hasData = false;
        
        $scope.$on('tabClicked', function(event, tab){
            if (tab != 'notificacaolote') return;
            
            if (!$scope.hasData) {
              $scope.start();
              $scope.hasData = true;
              
            } else {
                $scope.clear();
                $scope.clear();
                $scope.$broadcast('updateData', $rootScope.mestre.lotenotificacao, true, config.name);
            }
        });

        $scope.start = function() {
            var mestreID = angular.copy($rootScope.mestre.lotenotificacao);
            
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
                
                $scope.$broadcast('updateData', $scope.mestreID, true, config.name);
            });
            
            $scope.$on('aposPesquisarNoDetalhe' + config.name, function(event, obj){
                  $scope.total = obj.retorno.objectResponse;
            });

            $scope.$on('doClear' + config.name, function () {
                $scope.mestreID = {};
                $scope.mestreID = angular.copy($rootScope.mestre.lotenotificacao);
                $scope.clear();
                $scope.$broadcast('primeiraPagina', $scope.mestreID, true, config.name);
                $scope.view = true;
            });

        };
    }
]);