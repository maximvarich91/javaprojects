package ua.donntu.cs.java_course.rungecute1;

import java.lang.*;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main extends Thread
{
    private int nomer;
    public static double[] a=new double[3];//коэффициенты a
    public static double[] b=new double[3];//коэффициенты b
    public static double[] c=new double[3];//коэффициенты c
    public static double h;//шаг интегрирования
    public static double t0;//начальное время
    public static double tk;//конечное время
    public static double x_t0,y_t0,z_t0;//значение функций в начальное время
    public static double x_tk,y_tk,z_tk;//значение функций в конечное время
    public static double k1,k2,k3,k4;// для 1-го уравнения в системе
    public static double m1,m2,m3,m4;// для 2-го уравнения в системе
    public static double n1,n2,n3,n4;// для 3-го уравнения в системе
    public static double x_old,y_old,z_old;//старое значение функций
    public static double x_new,y_new,z_new;//новое значение функций
    public static double t;//текущее время

        public void run()
        {
             switch(nomer)
             {
                 case 1:  k1 = h * fx(a, t, x_old, y_old, z_old); break;
                 case 2:  k2 = h * fx(a, t+h/2.0, x_old+k1/2.0, y_old+m1/2.0, z_old+n1/2.0); break;
                 case 3:  k3 = h * fx(a, t+h/2.0, x_old+k2/2.0, y_old+m2/2.0, z_old+n2/2.0); break;
                 case 4:  k4 = h * fx(a, t+h, x_old+k3, y_old+m3, z_old+n3); break;
                 case 5:  m1 = h * fy(b, t, x_old, y_old, z_old); break;
                 case 6:  m2 = h * fy(b, t+h/2.0, x_old+k1/2.0, y_old+m1/2.0, z_old+n1/2.0); break;
                 case 7:  m3 = h * fy(b, t+h/2.0, x_old+k2/2.0, y_old+m2/2.0, z_old+n2/2.0); break;
                 case 8:  m4 = h * fy(b, t+h, x_old+k3, y_old+m3, z_old+n3); break;
                 case 9:  n1 = h * fz(c, t, x_old, y_old, z_old); break;
                 case 10:  n2 = h * fz(c, t+h/2.0, x_old+k1/2.0, y_old+m1/2.0, z_old+n1/2.0); break;
                 case 11:  n3 = h * fz(c, t+h/2.0, x_old+k2/2.0, y_old+m2/2.0, z_old+n2/2.0); break;
                 case 12:  n4 = h * fz(c, t+h, x_old+k3, y_old+m3, z_old+n3); break;
             }
        }

    public static void main(String[] args)
    {
        double eps=0.000000000001;//погрешность при сравнении вещественных чисел
        PrintWriter myoutfile = null;//выходной файл
        BufferedReader myinfile = null;//файл с исходными данными
        long time1,time2;
        int i;
        Main thread1;
        Main thread2;
        Main thread3;

        if(args.length<2)
         {
             System.out.println("Укажите в командной  строке [путь к файлу с исходными данными] [путь к выходному файлу]");
             System.exit(0);
         }

        try
        {
            myinfile= new BufferedReader(new FileReader(args[0]));
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Ошибка открытия файла с исходными данными:"+args[0]);
            System.exit(0);
        }

        read_from_file(myinfile);
        try
        {
        myinfile.close();
        }
        catch (IOException e)
        {
            System.out.println("Ошибка при закрытии файла с исходными данными");
            System.exit(0);
        }

        try
        {
         myoutfile = new PrintWriter(new FileOutputStream(args[1]));
        }
        catch(FileNotFoundException e)
        {
        System.out.println("Ошибка открытия выходного файла:"+args[1]);
        System.exit(0);
        }

        write_in_file(myoutfile);

        x_old=x_t0; y_old=y_t0; z_old=z_t0;
        x_new=0.0; y_new=0.0; z_new=0.0;
        myoutfile.print("\nИдет решение системы ДУ\n");
        time1=System.currentTimeMillis();
        for( t=t0; Math.abs(t-tk)>eps; t=t+h)
        {
            for(i=1;i<5;i++)
            {
                thread1=new Main();
                thread2=new Main();
                thread3=new Main();
                thread1.nomer=i;
                thread2.nomer=i+4;
                thread3.nomer=i+8;
                thread1.start();
                thread2.start();
                thread3.start();
                try
                {
                  thread1.join();
                  thread2.join();
                  thread3.join();
                } catch (InterruptedException x) {}
            }

            x_new = x_old + (k1 + 2.0*k2 + 2.0*k3 + k4)/6.0;
            y_new = y_old + (m1 + 2.0*m2 + 2.0*m3 + m4)/6.0;
            z_new = z_old + (n1 + 2.0*n2 + 2.0*n3 + n4)/6.0;

            myoutfile.println("t=" + myround(t+h,5) + " x=" + x_new + " y=" + y_new + " z=" + z_new);
            x_old=x_new; y_old=y_new; z_old=z_new;
        }
        x_tk=x_new; y_tk=y_new; z_tk=z_new;
        time2=System.currentTimeMillis();
        myoutfile.print("\nРешение системы ДУ получено\n");
        myoutfile.println("x("+myround(tk,5)+")="+myround(x_tk,5));
        myoutfile.println("y("+myround(tk,5)+")="+myround(y_tk,5));
        myoutfile.println("z("+myround(tk,5)+")="+myround(z_tk,5));
        myoutfile.println("Время работы цикла в миллисекундах "+(time2-time1));
        myoutfile.close();
    }

    public static void write_in_file(PrintWriter outfile)
    {
        int i;
        outfile.println("Исходные данные считанные из файла");

        outfile.print("коэффициенты");
        for(i=0;i<3;i++)
        {
        outfile.print(" a"+(i+1)+ "="+a[i]);
        }
        outfile.println("");

        outfile.print("коэффициенты");
        for(i=0;i<3;i++)
        {
            outfile.print(" b"+(i+1)+ "="+b[i]);
        }
        outfile.println("");

        outfile.print("коэффициенты");
        for(i=0;i<3;i++)
        {
            outfile.print(" c"+(i+1)+ "="+c[i]);
        }
        outfile.println("");

        outfile.println("шаг интегрирования h="+h);
        outfile.println("начальное время t0="+t0);
        outfile.println("конечное время tk="+tk);
        outfile.println("значение функций при t0");
        outfile.println("x("+t0+")="+x_t0+" y("+t0+")="+y_t0+" z("+t0+")="+z_t0);
    }

    public static void read_from_file(BufferedReader infile)
    {
        read_massiv(a,infile);
        read_massiv(b,infile);
        read_massiv(c,infile);
        h=read_double(infile);
        t0=read_double(infile);
        tk=read_double(infile);
        x_t0=read_double(infile);
        y_t0=read_double(infile);
        z_t0=read_double(infile);
    }

    public static void read_massiv(double[] array,BufferedReader infile)
    {
        int i;
        String mystr=null;
        String[] buf;
        try
        {
            mystr=infile.readLine();
        }
        catch (IOException e)
        {
            System.out.println("Ошибка при чтении из файла");
            System.exit(0);
        }

        if(mystr==null)
        {
            System.out.println("Ошибка при чтении из файла");
            System.exit(0);
        }

        buf=mystr.split(" ");
        if (buf.length!=3)
        {
            System.out.println("Ошибка при чтении из файла");
            System.exit(0);
        }
        for(i=0;i<3;i++)
        {
          array[i]=Double.parseDouble(buf[i]);
        }
    }

    public static double read_double(BufferedReader infile)
    {
        String mystr=null;
        double chislo;
        try
        {
            mystr=infile.readLine();
        }
        catch (IOException e)
        {
            System.out.println("Ошибка при чтении из файла");
            System.exit(0);
        }

        if(mystr==null)
        {
            System.out.println("Ошибка при чтении из файла");
            System.exit(0);
        }

        chislo=Double.parseDouble(mystr);
        return chislo;
    }


    public static double fx(double[] array, double t, double x, double y, double z)
    {
        /* try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException ex){} */

        return ((t*array[0]*x)+(array[1]*y)+(array[2]*z));
    }

    public static double fy(double[] array, double t, double x, double y, double z)
    {
        /* try
        {
        Thread.sleep(100);
        }
        catch (InterruptedException ex){} */

        return ((array[0]*x)+(t*array[1]*y)+(array[2]*z));
    }

    public static double fz(double[] array, double t, double x, double y, double z)
    {
        /* try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException ex){} */

        return ((array[0]*x)+(array[1]*y)+(t*array[2]*z));
    }

    public static double myround(double value, int k)
    {
        return Math.round((Math.pow(10, k)*value))/Math.pow(10, k);
    }
}
