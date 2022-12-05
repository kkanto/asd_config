def scripts = """

def job_config = [
        job: [
                name: "asd_Pipeline_develop",
                agent: "maven"
        ],
        git: [
                branch: "feature-1670246764559"
        ]
]

def config = [
        git: [
                protocol: "https",
                server: "github.developer.allianz.io",
                credentialsId: "git-token-credentials"
             ]
           ]

def lib = library identifier: 'BizDevOps_JSL@develop', retriever: modernSCM(
  [\$class: 'GitSCMSource',
   remote: 'https://github.developer.allianz.io/JEQP/BizDevOps-JSL.git',
   credentialsId: 'git-token-credentials']) 

def customLib = library identifier: 'asd_JSL@develop', retriever: modernSCM(
  [\$class: 'GitSCMSource',
   remote: 'https://github.developer.allianz.io/kkanto/asd_lib.git',
   credentialsId: 'git-token-credentials']) 
   
def jslGeneral    = lib.de.allianz.bdo.pipeline.JSLGeneral.new()
def jslGit        = lib.de.allianz.bdo.pipeline.JSLGit.new()
def jslGhe        = lib.de.allianz.bdo.pipeline.JSLGhe.new()

def jslCustom     = customLib.de.allianz.asd.new()

def manual_commit_sha

// for questions about this job ask mario akermann/tobias pfeifer from team pipeline

pipeline {
    agent { label job_config.job.agent }
    stages {
        stage('Prepare') {
            steps {
                echo "prepare"
                script {
                   jslGeneral.clean()
                }
            }    
        }
        stage('Checkout') {
            steps {
                echo "checkout"
                script {
                   jslGit.checkout( config, "kkanto", "asd", job_config.git.branch)
               }
            }    
        }
        stage('Build') {
            steps {
                echo "Build"
                script {
                      jslCustom.build()                  
                }
            }    
        }
        stage('Component Tests') {
            steps {
                echo "Component Tests"
                script {
                    jslCustom.componentTest()
                }
            }    
        }
        stage('Integration Tests') {
            steps {
                echo "Integration Tests"
                script {
                     jslCustom.integrationTest()
                }
            }    
        }
        stage('UAT Tests') {
            steps {
                echo "UAT Tests"
                script {
                        jslCustom.uatTest()
                }
            }    
        }
        stage('Acceptance Tests') {
            steps {
                echo "Acceptance Tests"
                script {
                        jslCustom.acceptanceTest()
                    }
                }
            }    
        stage('Publish Artifacts') {
            steps {
                echo "Publish Artifacts"
                script {
                        jslCustom.publishArtifacts()
                    }
                }
            }    
        stage('Publish Results') {
            steps {
                echo "Publish Results"
                script {
                        junit allowEmptyResults: true, testResults: '**/surefire-reports/TEST-*.xml'
                    }
                }
            }    
        }
    }
    
"""
        
def job = pipelineJob("asd_Pipeline_develop");

job.with {

    authenticationToken('dMX3m5L2473pVLKRNKbw6IATjWbwWhIv')
    
    definition {
        cps {
            script(scripts)
                sandbox()
        }
    }
}  
