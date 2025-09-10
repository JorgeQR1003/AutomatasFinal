using System;
using System.Collections.Generic;
using System.Security;
using AnalizadorLexico;

internal class Program
{
    //Se declaran los caracteres que se van a usar
    public static char[] specialCharacters = { '[', ']', '(', ')', '{', '}', ',', ';', '.', '+', '-', '*', '/', '%', '>', '<', '=', '!', '&', '|', '"' };
    public static char[] numeros = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    public static char[] letters = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    //Se establece el diccionario de transiciones del autómata tokenizador
    public static Dictionary<(string, char), string> transitionsA = new()
    {
        {("q0", '0'), "q1"}, {("q0", '+'), "q3"}, {("q0", '-'), "q4"}, {("q0", '='), "q5"}, {("q0", '<'), "q6"},
            {("q0", '>'), "q7"}, {("q0", '!'), "q14"}, { ("q0", '&'), "q16"}, { ("q0", '|'), "q18"}, { ("q0", 'b'), "q20"},
            {("q0", 'c'), "q28"}, { ("q0", 'C'), "q45"}, { ("q0", 'd'), "q58"}, { ("q0", 'D'), "q64"}, { ("q0", 'e'), "q73"},
            {("q0", 'E'), "q79"}, { ("q0", 'f'), "q84"}, { ("q0", 'G'), "q98"}, { ("q0", 'i'), "q106"}, { ("q0", 'I'), "q110"},
            {("q0", 'L'), "q121"}, { ("q0", 'M'), "q126"}, { ("q0", 'P'), "q130"}, { ("q0", 'r'), "q132"}, { ("q0", 'R'), "q133"},
            {("q0", 's'), "q143"}, { ("q0", 'S'), "q154"}, { ("q0", 't'), "q166"}, { ("q0", 'T'), "q167"}, { ("q0", 'w'), "q168"},
            {("q0", '@'), "q51"}, {("q0", '*'), "q52"}, {("q0", '('), "q13"}, {("q0", ')'), "q13"}, {("q0", '{'), "q13"}, 
            {("q0", '}'), "q13"}, {("q0", ','), "q13"}, {("q0", ';'), "q13"}, {("q0", '['), "q13"}, {("q0", ']'), "q13"},
            {("q0", '/'), "q13"}, {("q0", '%'), "q13"}, {("q0", '"'), "q179"},
        {("q1", '0'), "q1"}, {("q1", '.'), "q2"},
        {("q2", '0'), "q2"},
        {("q3", '+'), "q8"},
        {("q4", '-'), "q9"},
        {("q5", '='), "q10"},
        {("q6", '='), "q11"}, {("q6", '/'), "q181"},
        {("q7", '='), "q12"},
        {("q14", '='), "q15"},
        {("q16", '&'), "q17"},
        {("q18", '|'), "q19"},
        {("q20", 'o'), "q21"}, {("q20", 'r'), "q24"}, {("q20", '@'), "q51"},
        {("q21", 'o'), "q22"}, {("q21", '@'), "q51"},
        {("q22", 'l'), "q23"}, {("q22", '@'), "q51"},
        {("q23", '@'), "q51"},
        {("q24", 'e'), "q25"}, {("q24", '@'), "q51"},
        {("q25", 'a'), "q26"}, {("q25", '@'), "q51"},
        {("q26", 'k'), "q27"}, {("q26", '@'), "q51"},
        {("q27", '@'), "q51"},
        {("q28", 'a'), "q29"}, {("q28", 'h'), "q35"}, {("q28", 'o'), "q38"}, {("q28", '@'), "q51"},
        {("q29", 's'), "q30"}, {("q29", 't'), "q32"}, {("q29", '@'), "q51"},
        {("q30", 'e'), "q31"}, {("q30", '@'), "q51"},
        {("q31", '@'), "q51"},
        {("q32", 'c'), "q33"}, {("q32", '@'), "q51"},
        {("q33", 'h'), "q34"}, {("q33", '@'), "q51"},
        {("q34", '@'), "q51"},
        {("q35", 'a'), "q36"}, {("q35", '@'), "q51"},
        {("q36", 'r'), "q37"}, {("q36", '@'), "q51"},
        {("q37", '@'), "q51"},
        {("q38", 'n'), "q39"}, {("q38", '@'), "q51"},
        {("q39", 't'), "q40"}, {("q39", '@'), "q51"},
        {("q40", 'i'), "q41"}, {("q40", '@'), "q51"},
        {("q41", 'n'), "q42"}, {("q41", '@'), "q51"},
        {("q42", 'u'), "q43"}, {("q42", '@'), "q51"},
        {("q43", 'e'), "q44"}, {("q43", '@'), "q51"},
        {("q44", '@'), "q51"},
        {("q45", 'o'), "q46"}, {("q45", 's'), "q56"}, {("q45", '@'), "q51"},
        {("q46", 'n'), "q47"}, {("q46", 's'), "q55"}, {("q46", '@'), "q51"},
        {("q47", 'c'), "q48"}, {("q47", 's'), "q53"}, {("q47", '@'), "q51"},
        {("q48", 'a'), "q49"}, {("q48", '@'), "q51"},
        {("q49", 't'), "q50"}, {("q49", '@'), "q51"},
        {("q50", '@'), "q51"},
        {("q51", '@'), "q51"}, {("q51", '0'), "q51"},
        {("q52", '*'), "q178"},
        {("q53", 't'), "q54"}, {("q53", '@'), "q51"},
        {("q54", '@'), "q51"},
        {("q55", '@'), "q51"},
        {("q56", 'c'), "q57"}, {("q56", '@'), "q51"},
        {("q57", '@'), "q51"},
        {("q58", 'o'), "q59"}, {("q58", '@'), "q51"},
        {("q59", 'u'), "q60"}, {("q59", '@'), "q51"},
        {("q60", 'b'), "q61"}, {("q60", '@'), "q51"},
        {("q61", 'l'), "q62"}, {("q61", '@'), "q51"},
        {("q62", 'e'), "q63"}, {("q62", '@'), "q51"},
        {("q63", '@'), "q51"},
        {("q64", 'e'), "q65"}, {("q64", 'i'), "q70"}, {("q64", '@'), "q51"},
        {("q65", 'r'), "q66"}, {("q65", '@'), "q51"},
        {("q66", 'i'), "q67"}, {("q66", '@'), "q51"},
        {("q67", 'v'), "q68"}, {("q67", '@'), "q51"},
        {("q68", 'X'), "q69"}, {("q68", '@'), "q51"},
        {("q69", '@'), "q51"},
        {("q70", 's'), "q71"}, {("q70", '@'), "q51"},
        {("q71", 't'), "q72"}, {("q71", '@'), "q51"},
        {("q72", '@'), "q51"},
        {("q73", 'l'), "q74"}, {("q73", '@'), "q51"},
        {("q74", 'i'), "q75"}, {("q74", 's'), "q77"}, {("q74", '@'), "q51"},
        {("q75", 'f'), "q76"}, {("q75", '@'), "q51"},
        {("q76", '@'), "q51"},
        {("q77", 'e'), "q78"}, {("q77", '@'), "q51"},
        {("q78", '@'), "q51"},
        {("q79", 'U'), "q80"}, {("q79", '@'), "q51"},
        {("q80", 'L'), "q81"}, {("q80", '@'), "q51"},
        {("q81", 'E'), "q82"}, {("q81", '@'), "q51"},
        {("q82", 'R'), "q83"}, {("q82", '@'), "q51"},
        {("q83", '@'), "q51"},
        {("q84", 'o'), "q85"}, {("q84", 'u'), "q91"}, {("q84", '@'), "q51"},
        {("q85", 'r'), "q86"}, {("q85", '@'), "q51"},
        {("q86", 'e'), "q87"}, {("q86", '@'), "q51"},
        {("q87", 'a'), "q88"}, {("q87", '@'), "q51"},
        {("q88", 'c'), "q89"}, {("q88", '@'), "q51"},
        {("q89", 'h'), "q90"}, {("q89", '@'), "q51"},
        {("q90", '@'), "q51"},
        {("q91", 'n'), "q92"}, {("q91", '@'), "q51"},
        {("q92", 'c'), "q93"}, {("q92", '@'), "q51"},
        {("q93", 't'), "q94"}, {("q93", '@'), "q51"},
        {("q94", 'i'), "q95"}, {("q94", '@'), "q51"},
        {("q95", 'o'), "q96"}, {("q95", '@'), "q51"},
        {("q96", 'n'), "q97"}, {("q96", '@'), "q51"},
        {("q97", '@'), "q51"},
        {("q98", 'e'), "q99"}, {("q98", 'r'), "q102"}, {("q98", '@'), "q51"},
        {("q99", 'n'), "q100"}, {("q99", '@'), "q51"},
        {("q100", 'f'), "q101"}, {("q100", '@'), "q51"},
        {("q101", '@'), "q51"},
        {("q102", 'a'), "q103"}, {("q102", '@'), "q51"},
        {("q103", 'p'), "q104"}, {("q103", '@'), "q51"},
        {("q104", 'h'), "q105"}, {("q104", '@'), "q51"},
        {("q105", '@'), "q51"},
        {("q106", 'f'), "q107"}, {("q106", 'n'), "q108"}, {("q106", '@'), "q51"},
        {("q107", '@'), "q51"},
        {("q108", 't'), "q109"}, {("q108", '@'), "q51"},
        {("q109", '@'), "q51"},
        {("q110", 'N'), "q111"}, {("q110", 'n'), "q113"}, {("q110", '@'), "q51"},
        {("q111", 'F'), "q112"}, {("q111", '@'), "q51"},
        {("q112", '@'), "q51"},
        {("q113", 't'), "q114"}, {("q113", 'p'), "q118"}, {("q113", '@'), "q51"},
        {("q114", 'e'), "q115"}, {("q114", '@'), "q51"},
        {("q115", 'g'), "q116"}, {("q115", '@'), "q51"},
        {("q116", 'X'), "q117"}, {("q116", '@'), "q51"},
        {("q117", '@'), "q51"},
        {("q118", 'u'), "q119"}, {("q118", '@'), "q51"},
        {("q119", 't'), "q120"}, {("q119", '@'), "q51"},
        {("q120", '@'), "q51"},
        {("q121", 'i'), "q122"}, {("q121", '@'), "q51"},
        {("q122", 'm'), "q123"}, {("q122", '@'), "q51"},
        {("q123", 'i'), "q124"}, {("q123", '@'), "q51"},
        {("q124", 't'), "q125"}, {("q124", '@'), "q51"},
        {("q125", '@'), "q51"},
        {("q126", 'a'), "q127"}, {("q126", '@'), "q51"},
        {("q127", 'i'), "q128"}, {("q127", '@'), "q51"},
        {("q128", 'n'), "q129"}, {("q128", '@'), "q51"},
        {("q129", '@'), "q51"},
        {("q130", 'I'), "q131"}, {("q130", '@'), "q51"},
        {("q131", '@'), "q51"},
        {("q132", 'e'), "q134"}, {("q132", '@'), "q51"},
        {("q133", 'o'), "q139"}, {("q133", '@'), "q51"},
        {("q134", 't'), "q135"}, {("q134", '@'), "q51"},
        {("q135", 'u'), "q136"}, {("q135", '@'), "q51"},
        {("q136", 'r'), "q137"}, {("q136", '@'), "q51"},
        {("q137", 'n'), "q138"}, {("q137", '@'), "q51"},
        {("q138", '@'), "q51"},
        {("q139", 'o'), "q140"}, {("q139", '@'), "q51"},
        {("q140", 't'), "q141"}, {("q140", '@'), "q51"},
        {("q141", 'X'), "q142"}, {("q141", '@'), "q51"},
        {("q142", '@'), "q51"},
        {("q143", 't'), "q144"}, {("q143", 'w'), "q149"}, {("q143", '@'), "q51"},
        {("q144", 'r'), "q145"}, {("q144", '@'), "q51"},
        {("q145", 'i'), "q146"}, {("q145", '@'), "q51"},
        {("q146", 'n'), "q147"}, {("q146", '@'), "q51"},
        {("q147", 'g'), "q148"}, {("q147", '@'), "q51"},
        {("q148", '@'), "q51"},
        {("q149", 'i'), "q150"}, {("q149", '@'), "q51"},
        {("q150", 't'), "q151"}, {("q150", '@'), "q51"},
        {("q151", 'c'), "q152"}, {("q151", '@'), "q51"},
        {("q152", 'h'), "q153"}, {("q152", '@'), "q51"},
        {("q153", '@'), "q51"},
        {("q154", 'e'), "q155"}, {("q154", 'h'), "q157"}, {("q154", 'i'), "q160"}, {("q154", 'l'), "q161"}, {("q154", '@'), "q51"},
        {("q155", 'c'), "q156"}, {("q155", '@'), "q51"},
        {("q156", '@'), "q51"},
        {("q157", 'o'), "q158"}, {("q157", '@'), "q51"},
        {("q158", 'w'), "q159"}, {("q158", '@'), "q51"},
        {("q159", '@'), "q51"},
        {("q160", 'n'), "q162"}, {("q160", '@'), "q51"},
        {("q161", 'o'), "q163"}, {("q161", '@'), "q51"},
        {("q162", '@'), "q51"},
        {("q163", 'p'), "q164"}, {("q163", '@'), "q51"},
        {("q164", 'e'), "q165"}, {("q164", '@'), "q51"},
        {("q165", '@'), "q51"},
        {("q166", 'r'), "q169"}, {("q166", '@'), "q51"},
        {("q167", 'a'), "q171"}, {("q167", '@'), "q51"},
        {("q168", 'h'), "q173"}, {("q168", '@'), "q51"},
        {("q169", 'y'), "q170"}, {("q169", '@'), "q51"},
        {("q170", '@'), "q51"},
        {("q171", 'n'), "q172"}, {("q171", 'b'), "q174"}, {("q171", '@'), "q51"},
        {("q172", '@'), "q51"},
        {("q173", 'i'), "q175"}, {("q173", '@'), "q51"},
        {("q174", '@'), "q51"},
        {("q175", 'l'), "q176"}, {("q175", '@'), "q51"},
        {("q176", 'e'), "q177"}, {("q176", '@'), "q51"},
        {("q177", '@'), "q51"},
        {("q179", '#'), "q179"}, {("q179", '"'), "q180"},
        {("q181", '#'), "q181"}, {("q181", '>'), "q182"},
    };

    //Se establecen los diferentes estados finales
    public static string[] finalNum = { "q1", "q2" };
    public static string[] finalExact = { "q3", "q4", "q5", "q6", "q7", "q8", "q9",
        "q10", "q11", "q12", "q13", "q14", "q15", "q17", "q19", "q23", "q27", "q34",
        "q37", "q44", "q50", "q54", "q55", "q57", "q63", "q68", "q69", "q72", "q76",
        "q78", "q83", "q86", "q13", "q14", "q15", "q17", "q19", "q23", "q27", "q34",
        "q10", "q11", "q90", "q97", "q101", "q105", "q107", "q112", "q117", "q120", 
        "q125", "q129", "q131", "q138", "q141", "q142", "q148", "q153", "q156", "q159",
        "q162", "q165", "q170", "q172", "q174", "q177" };
    public static string[] finalLiteralString = { "q180" };
    public static string[] finalComment = { "q182" };
    public static string[] notFinal = { "q16", "q18" };

    //Metodo para tokenizar un string
    public static List<string> GetTokens(string input)
    {
        List<string> tokens = new List<string>();
        char x = '.';
        string temp = "";
        string initialState = "q0";
        string currentState = initialState;

        //Se recorre el string y se va tokenizando
        for (int i = 0; i < input.Length; i++)
        {
            char c = input[i];

            x = '&';

            if (currentState == "q179")
            {
                if (c == '"')
                    x = '"';
                else
                    x = '#';
            }
            else if (currentState == "q181")
            {
                if (c == '>')
                    x = '>';
                else
                    x = '#';
            }
            else if (numeros.Contains(c))
                x = '0';
            else if (specialCharacters.Contains(c) || letters.Contains(c))
                x = c;
            else if (c == ' ')
            {
                addToken(tokens, initialState, currentState, temp);

                temp = string.Empty;
                currentState = initialState;
                continue;
            }
            else
            {
                //En caso de que el caracter no pertenezca al lenguaje se regresa una lista vacia, indicando que hubo un error
                Console.WriteLine("Error: Caracter no perteneciente al lenguaje");
                return new List<string>();
            }

            if (transitionsA.TryGetValue((currentState, x), out string nextState))
            {
                currentState = nextState;
                temp += c;
            }
            else
            {
                if (letters.Contains(c) || numeros.Contains(c))
                {
                    x = '@';
                    if (transitionsA.TryGetValue((currentState, x), out string next))
                        currentState = next;
                    else
                    {
                        if (transitionsA.TryGetValue((initialState, x), out string nextTemp) && !((numeros.Contains(input[i-1])) && (letters.Contains(c))))
                        {
                            addToken(tokens, initialState, currentState, temp);
                            currentState = initialState;
                            temp = string.Empty;
                            i--;
                        }
                        else
                        {
                            Console.WriteLine("Error: Transicion no valida");
                            return new List<string>();
                        }
                    }
                }
                else
                {
                    if (transitionsA.TryGetValue((initialState, x), out string nextTemp) && !((numeros.Contains(input[i-1])) && (letters.Contains(c))))
                    {
                        addToken(tokens, initialState, currentState, temp);
                        currentState = initialState;
                        temp = string.Empty;
                        i--;
                    }
                    else
                    {
                        Console.WriteLine("Error: Transicion no valida");
                        return new List<string>();
                    }
                }
            }
        }

        addToken(tokens, initialState, currentState, temp);

        return tokens;
    }

    public static void addToken(List<string> tokens, string initialState, string currentState, string temp)
    {
        if (finalExact.Contains(currentState))
            tokens.Add(temp);
        else if (finalNum.Contains(currentState))
            tokens.Add("NUM");
        else if (finalLiteralString.Contains(currentState))
            tokens.Add("LITERAL");
        else if (finalComment.Contains(currentState)) { }
        else if (notFinal.Contains(currentState) || initialState == currentState)
        {
            //Si se queda en un estado no final y encuentra un espacio, no pertenece al lenguaje
            Console.WriteLine("Error: Cadena incompleta");
            Console.WriteLine("Cadena: " + temp);
            tokens = new List<string>();
        }
        else
            //Se quedo un estado final para un ID
            tokens.Add("ID");
    }

    static void Main(string[] args)
    {
        Scanner scanner = new Scanner("/Users/gibran/RiderProjects/AutomatasFinal2/FilePruebas.txt");

       
        //Se hace un lista de tokens con el string
        List<string> tokens = GetTokens(scanner.getInput());

        //Se verifica si la lista de tokens es vacia
        if (tokens.Count == 0)
        {
            return;
        }

        //Se imprime la lista de tokens
        Console.WriteLine("Expresion tokenizada: ");
        Console.Write("[");
        foreach (string token in tokens)
        {
            Console.Write($" {token} ");
        }
        Console.Write("]");
        Console.WriteLine();

        Console.ReadKey();
    }
}