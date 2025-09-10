namespace AnalizadorLexico;

public class Scanner
{
    private string input;
    private string file;

    public Scanner(string file)
    {
        this.file = file;
        input = CleanInput(file);
    }

    public string CleanInput(string file)
    {

        var wholeFile = File.ReadLines(file);
        string codeAsAString = "";
        foreach (var line in wholeFile)
        {
            
            var formattedLine = line.Trim() + " ";
            Console.WriteLine(formattedLine);
            formattedLine = (formattedLine == " ") ? "" : formattedLine;
            codeAsAString += formattedLine;


        }
     
        return codeAsAString.TrimEnd();
    }
    
    public string getInput()
    {
        return input;
    }
    
}