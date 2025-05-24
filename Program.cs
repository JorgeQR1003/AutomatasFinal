using System;
using System.Collections.Generic;

internal class Program
{
    //Se declaran los caracteres que se van a usar
    public static char[] operadoresAsterisk = { '/', '*', '=' };
    public static char[] operadoresDollar = { '-', '+' };
    public static char[] numeros = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    public static char[] parentesis = { '(', ')' };
    public static char[] letras = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

    //Se establece el diccionario de transiciones del autómata tokenizador
    public static Dictionary<(string, char), string> transitionsA = new()
    {
        {("q0", 'a'), "q4"}, {("q0", '0'), "q1"}, {("q0", '*'), "q2"}, {("q0", '$'), "q3"}, {("q0", ')'), "q5"},
        {("q1", '0'), "q1"}, {("q1", '$'), "q6"},
        {("q3", '0'), "q1"},
        {("q4", '0'), "q4"}, {("q4", 'a'), "q4"}, {("q4", '$'), "q8"},
        {("q5", '$'), "q7"},
    };

    //Se establecen los diferentes estados finales
    public static string finalID = "q4";
    public static string finalNUM = "q1";
    public static string finalOP = "q2";
    public static string finalPM = "q3";
    public static string finalPM2 = "q6";
    public static string finalP = "q7";
    public static string finalPSolo = "q5";
    public static string finalPM3 = "q8";

    //Metodo para tokenizar un string
    public static List<string> GetTokens(string input)
    {
        List<string> tokens = new List<string>();
        char x = '.';
        char lastC = '.';
        string temp = "";
        string initialState = "q0";
        string currentState = initialState;

        //Se recorre el string y se va tokenizando
        for (int i = 0; i < input.Length; i++)
        {
            char c = input[i];

            x = '.';

            if (letras.Contains(c))
                x = 'a';
            else if (numeros.Contains(c))
                x = '0';
            else if (operadoresAsterisk.Contains(c) || c == '(')
                x = '*';
            else if (operadoresDollar.Contains(c))
                x = '$';
            else if (c == ')')
                x = ')';
            else if (c == ' ')
            {
                continue;
            }
            else
            {
                //En caso de que el caracter no pertenezca al lenguaje se regresa una lista vacia, indicando que hubo un error
                Console.WriteLine("Error: Caracter no perteneciente al lenguaje");
                return new List<string>();
            }

            //Se translada entre el diccionario de estados
            if (transitionsA.TryGetValue((currentState, x), out string nextState))
            {
                currentState = nextState;
                temp += c;
            }
            else
            {
                //Se verifica a que estado final se llego para poder agregar el respectivo token
                if (!string.IsNullOrEmpty(temp) && finalP == currentState)
                {
                    tokens.Add(")");
                    tokens.Add(lastC.ToString());
                }
                else if (!string.IsNullOrEmpty(temp) && finalPM2 == currentState)
                {
                    tokens.Add("num");
                    tokens.Add(lastC.ToString());
                }
                else if (!string.IsNullOrEmpty(temp) && finalPM3 == currentState)
                {
                    tokens.Add("id");
                    tokens.Add(lastC.ToString());
                }
                else if (!string.IsNullOrEmpty(temp) && finalPM == currentState)
                    tokens.Add(temp);
                else if (!string.IsNullOrEmpty(temp) && finalID == currentState)
                    tokens.Add("id");
                else if (!string.IsNullOrEmpty(temp) && finalNUM == currentState)
                    tokens.Add("num");
                else if (!string.IsNullOrEmpty(temp) && finalOP == currentState)
                    tokens.Add(temp);
                else if (!string.IsNullOrEmpty(temp) && finalPSolo == currentState)
                    tokens.Add(temp);

                temp = "";
                currentState = initialState;

                //Por si el caracter si se puede tokenizar pero no pegado al caracter anterior
                i--;
            }
            lastC = c;
        }
        //Se verifica si se quedo en un estado final
        if (finalID == currentState)
            tokens.Add("id");
        else if (finalNUM == currentState)
            tokens.Add("num");
        else if (finalOP == currentState)
            tokens.Add(temp);
        else if (finalPM == currentState)
            tokens.Add(temp);
        else if (finalPM2 == currentState)
            tokens.Add(temp);
        else if (finalP == currentState)
            tokens.Add(temp);
        else if (finalPSolo == currentState)
            tokens.Add(temp);

        return tokens;
    }

    //Se declaran los operadores
    public static string[] op = { "-", "+", "/", "*" };

    //Se declara el diccionario de transiciones del autómata para validar tokens
    public static Dictionary<(string, string, string, string), string> transitionsB = new()
    {
        {("q0", "id", "&", "&"), "q1"},
        {("q1", "=", "&", "&"), "q2"},
        {("q2", "id", "&", "&"), "q4"}, {("q2", "num", "&", "&"), "q4"}, {("q2", "-", "&", "&"), "q3"}, {("q2", "(", "&", "("), "q7"}, {("q2", "+", "&", "&"), "q3"},
        {("q3", "num", "&", "&"), "q4"}, {("q3", "(", "&", "("), "q2"},
        {("q4", "op", "&", "&"), "q5"}, {("q4", "(", "&", "("), "q8"}, {("q4", ")", "(", "&"), "q4"},
        {("q5", "(", "&", "("), "q5"}, {("q5", "id", "&", "&"), "q6"}, {("q5", "num", "&", "&"), "q6"},
        {("q6", ")", ")", "&"), "q6"}, {("q6", "op", "&", "&"), "q5"},
        {("q7", "id", "&", "&"), "q4"}, {("q7", "num", "&", "&"), "q4"}, {("q7", "(", "&", "("), "q7"},
        {("q8", "id", "&", "&"), "q6"}, {("q8", "num", "&", "&"), "q6"},
    };

    public static string finalState = "q6";

    public static bool isPartOfLanguage(List<string> tokens)
    {
        Stack<string> stack = new Stack<string>();
        string stackIn = "";
        string stackOut = "";
        string initialState = "q0";
        string currentState = initialState;

        for (int i = 0; i < tokens.Count; i++)
        {
            //Se les da este valor a las variables para que puedan moverse por el diccionario
            stackIn = "&";
            stackOut = "&";

            string s = tokens[i];

            //Si el caracter es un parentesis, hace su respectivo push o pop a la pila
            if (s == "(") {
                stack.Push(s);
                stackIn = "(";
            }
            else if (s == ")")
            {
                try
                {
                    stackOut = stack.Pop();
                }
                catch (InvalidOperationException)
                {
                    //Si la pila esta vacia y se intenta hacer un pop, regresa falso
                    return false;
                }
                stackOut = ")";
            }
            //Si el caracter es un operador, se le asigna "op" 
            else if (op.Contains(s))
            {
                s = "op";
            }

            //Se mueve entre los estados del diccionario
            if (transitionsB.TryGetValue((currentState, s, stackOut, stackIn), out string nextState))
                currentState = nextState;
            else
                //Si no se encuentra la transicion, regresa falso ya que no es parte del lenguaje
                return false;
        }

        //Se verifica si la pila esta vacia y si termino en estado final
        if (stack.Count == 0 && currentState == finalState)
            return true;
        else
            return false;
    }

    static async Task Main(string[] args)
    {
        string input = "";

        Console.WriteLine("Ingresa una expresion: ");
        input = Console.ReadLine();

        //Se hace un lista de tokens con el string
        List<string> tokens = GetTokens(input.ToLower());

        //Se verifica si la lista de tokens es vacia
        if (tokens.Count == 0)
        {
            Console.WriteLine("Error: No se encontraron caracteres");
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

        //Se verifica si la lista de tokens es parte del lenguaje
        if (isPartOfLanguage(tokens))
            Console.WriteLine("Expresion aceptada");
        else
            Console.WriteLine("Expresion no aceptada");
    }
}