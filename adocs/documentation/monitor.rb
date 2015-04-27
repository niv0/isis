#!/c/bin/Ruby200-x64/bin/ruby -w

require 'rubygems'
require 'pathname'
require 'fileutils'
require 'bundler/setup'

require 'webrick'
include WEBrick

Bundler.require(:default)

# uses: https://github.com/guard/listen

# to suppress some debugs
$CELLULOID_DEBUG=false
$CELLULOID_TEST=false

#scriptDir = File.absolute_path File.dirname(__FILE__)
templateDir = File.absolute_path '../template'
srcBaseDir = File.absolute_path 'src/main/asciidoc'
targetBaseDir = File.absolute_path 'target/site'

srcBasePath = Pathname.new srcBaseDir
targetBasePath = Pathname.new targetBaseDir

puts ""
puts ""
puts ""
puts "now monitoring..."
puts ""

i=0

def process(file,srcBasePath,targetBasePath,templateDir,i)

    workingDir = Dir.pwd

    srcDir = File.dirname file
    srcBase = File.basename file
    ext = File.extname file

    srcPath = Pathname.new srcDir
    srcRel = srcPath.relative_path_from srcBasePath

    targetRelPath = targetBasePath + srcRel
    targetRelDir = File.absolute_path targetRelPath.to_s

    Dir.chdir srcDir
    FileUtils.mkdir_p targetRelDir

    if ext == ".adoc" then

        srcSplit = srcBase.split('_')
        if srcSplit[0].length==0 then
            # handle include files of form
            # _xxx-xxx_yyy-yyy_zzz  => xxx-xxx.adoc
            regenerate = srcSplit[1] + ".adoc"
        else
            regenerate = srcBase
        end

        cmd = "asciidoctor #{regenerate} --backend html --eruby erb --template-dir '#{templateDir}' --destination-dir='#{targetRelDir}' -a imagesdir='' -a toc=right -a icons=font -a source-highlighter=coderay"

        puts ""
        puts "#{i}: #{cmd}"

        system cmd

    else

        cmd = "cp #{srcBase} #{targetRelDir}"

        puts ""
        puts "#{i}: #{cmd}"

        system cmd

    end

    Dir.chdir workingDir

    return i+1

end

# process all files
files = Dir.glob("src/main/asciidoc/**/*")
files.each { |file|
    i = process file, srcBasePath, targetBasePath, templateDir, i
}

# then continue monitoring all directories
directories = Dir.glob("src/main/asciidoc/**/*/")
listener = Listen.to(directories) do |modified, added, removed|
    unless modified.length==0
        modified.each { |file|
            i = process file, srcBasePath, targetBasePath, templateDir, i
        }
    end
    unless added.length==0
        added.each { |file|
            i = process file, srcBasePath, targetBasePath, templateDir, i
        }
    end
    unless removed.length==0
        removed.each { |file|
            puts "removed #{file}"
      
        }
    end
end
listener.start
#listener.only(/.*\.adoc$/)

s = HTTPServer.new(:Port => 4000,  :DocumentRoot => 'target/site')
trap("INT"){ s.shutdown }
s.start
