angular.module('processojudicialpa', ['ngRoute'])

.constant("processojudicialpaConfig", {
    name: 'processojudicialpa',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/dadoprocessojudicials/search'
    },
    model: [
        {field:"id", hidden: "hide"},
        {field:"entidade.autoInfracao", displayName:"processojudicialpa.label.autoInfracao" },
        {field:"entidade.codigoInfracao", displayName:"processojudicialpa.label.codigoInfracao" },
        {field:"entidade.dataInfracao", displayName:"processojudicialpa.label.dataInfracao" },
        {field:"orgaoAutuadorLabel", displayName:"processojudicialpa.label.orgaoAutuador" },
        {field:"origemInfracaoLabel", displayName:"processojudicialpa.label.origemInfracao" },
        {field:"entidade.quantidadePontosInfracao", displayName:"processojudicialpa.label.quantidadePontosInfracao" },
        {field:"entidade.placa", displayName:"processojudicialpa.label.placa" },
        {field: "actions", actions: [
          {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"}
        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processojudicialpa',
    urlPathMestre: '/adm/processoadministrativo',
    hasMestre: true,
    i18nextResources : {
        "processojudicialpa" : {
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
                "segredoJustica": "Segredo Justica",
                "dataInicioPenalidade": "Data Início Penalidade"
            }
        }
    }
})

.controller('processojudicialpaCtrl', ['$scope', '$injector', 'processojudicialpaConfig', 'srvDetranAbstractResourceRest', '$rootScope', 'srvTable', 'growl',
    function ($scope, $injector, config, srvRest, $rootScope, srvTable, growl) {
        $injector.invoke(ctrlFiltroDtl, this, {$scope: $scope, $rootScope: $rootScope, config: config});
        $scope.showTabs = false;
        
        $scope.hasData = false;
        
        $scope.$on('tabClicked', function(event, tab){
            console.log("passei", tab);
            if (tab != 'processojudicialpa') return;
            
            if (!$scope.hasData) {
              $scope.start();
              $scope.hasData = true;
              
            } else {
                $scope.clear();
                pesquisar($scope.mestreID);
            }
        });

        $scope.start = function() {
            var mestreID = angular.copy($rootScope.mestre.processoadministrativo);
            
            if (mestreID == undefined || mestreID == '') return;
            
            $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
            $injector.invoke(mestreDetalheBasicCtrl, this, {$scope: $scope, config: config});
        
            $scope.urlPath = $scope.$parent.urlPath;
            
            $scope.mestreID = mestreID;
            $scope.config = config;

            pesquisar(mestreID);
            

        };

        function pesquisar(mestreID){
            $scope.editar(config.urls.pesquisar, {idPA:mestreID.processoAdministrativo.id}).then(function (retorno) {

                if (!_.isEmpty(retorno.error)) {

                    detranUtil.showMsg(growl,retorno.error);

                } else {

                    $scope.data = retorno.entity[0];
                }
            }, function (err) {
                detranUtil.showMsg(growl,err.error);
            });
        };
    }
]);