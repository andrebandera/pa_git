angular.module('cadastroretornoar', ['ngRoute'])

.constant("cadastroretornoarConfig", {
    name: 'cadastroretornoar',
    urls: {
        "gravar": 'detran-processo-administrativo/resource/cadastroretornoars/save',
        "buscarNotificacao": 'detran-processo-administrativo/resource/cadastroretornoars/search',
        "novo": 'detran-processo-administrativo/resource/cadastroretornoars/new'
    },
    filesPath: '/detran-processo-administrativo/site/modulos/adm/cadastroretornoar',
    urlPath: '/adm/cadastroretornoar',
    i18nextResources: {
        "cadastroretornoar":{
            "titulo":"Cadastro Retorno AR - Lógico", 
            "label": {
                "objetoCorreios":"Número AR", 
                "andamento": "Andamento",
                "dataEmissaoAR": "Data Emissão AR",
                "dataPostagem": "Data Postagem AR",
                "situacaoEntrega": "Situação Retorno AR",
                "dataRecebimento": "Data Recebimento / Devolução",
                "nomeRecebedor": "Nome Recebedor",
                "documentoRecebedor": "Documento Recebedor",
                "numeroProcesso": "Número Processo",
                "numeroNotificacao": "Número Notificação",
                "tipoNotificacao": "Tipo Notificação",
                
            }
        }
    }
})

.config(["$routeProvider", "cadastroretornoarConfig", function ($rp, config) {
  var path      = config.filesPath;
  var urlPath   = config.urlPath;
  var name      = config.name;

  $rp
    .when(urlPath, {templateUrl: path + '/index.html', controller: name + 'Create', label: 'cadastroretornoar.titulo'});
}])

.controller('cadastroretornoarCtrl', ["$scope", "$injector", "cadastroretornoarConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('cadastroretornoarCreate', ['$scope', '$controller', '$injector', "cadastroretornoarConfig",
    function($scope, $controller, $injector, config){
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
        $scope.page = 'create';
        $controller('cadastroretornoarCustomCtrl', {$scope: $scope});
    }
])

.controller('cadastroretornoarCustomCtrl', ["$scope", "$controller", "cadastroretornoarConfig", "$injector", "growl",
    function($scope, ctrl, config, $injector, growl) {
        
        ctrl('cadastroretornoarCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
         
        $scope.data = {
            entidade:{}
        };
        
        $scope.aux = {};
        
        $scope.buscarNotificacao = function(){
            
            $scope.carregar(config.urls.buscarNotificacao, {objetoCorreios: $scope.aux.objetoCorreios}).then(function (retorno) {
                
                if (retorno.error != "") {
                    detranUtil.showMsg(growl,retorno.error);
                }else if (retorno.warning != "" && angular.isDefined(retorno.warning)) {
                    detranUtil.showMsgWarn(growl,retorno.warning);
                }else{
                    $scope.data = retorno.entity[0];
                }    
            });
        }
        
        $scope.preSave = function(){
            
            if (!angular.isUndefined($scope.data)) {
                if (!detranUtil.ehBrancoOuNulo($scope.data.correspondenciaCorreioDevolucao)) {
                    var andamentoAux = angular.copy(angular.fromJson($scope.data.correspondenciaCorreioDevolucao));
                    $scope.data.correspondenciaCorreioDevolucao = andamentoAux;
                }
            }
            
            $scope.save(true, true);
        };
        
        $scope.validateForm = function() {
                
            if (!$scope.dataChanged()) 
                return true;

            if (!detranUtil.ehBrancoOuNulo($scope.data)) {

                if (!detranUtil.ehBrancoOuNulo($scope.data.entidade)
                        && !detranUtil.ehBrancoOuNulo($scope.data.correspondenciaCorreio)) {

                    if (!detranUtil.ehBrancoOuNulo($scope.data.correspondenciaCorreio.nomeRecebedor)
                            && !detranUtil.ehBrancoOuNulo($scope.data.correspondenciaCorreio.documentoRecebedor)
                            && !detranUtil.ehBrancoOuNulo($scope.data.correspondenciaCorreio.dataChegadaDestino)
                            && !detranUtil.ehBrancoOuNulo($scope.data.correspondenciaCorreioDevolucao)) {
                        return false;
                    }
                }
            }

            return true;
        };
        
        $scope.$on('savedData' + config.name, function(event, data){
            $scope.new();
        });
        
        $scope.new = function(){
            $scope.data = {
                entidade:{}
            };
            $scope.aux = {};
        }
    }
]);
