package org.CreadoresProgram.CraftsMine;
import org.CreadoresProgram.CraftsMine.server.Server;
import org.CreadoresProgram.CraftsMine.inventory.Item;
import org.CreadoresProgram.CraftsMine.nukkitLib.entity.Attribute;
public class CraftsMine{
  public static String DATA_PATH = System.getProperty("user.dir") + "/";
  public static void main(String[] args){
    Thread.currentThread().setName("CraftsMine-main");
    System.out.println("Starting CraftsMine Proxy Software Prototype");
    System.setProperty("java.compiler", "javac");
    // Use Netty's default pooled allocator. The previous code forced
    // io.netty.allocator.type=unpooled which made every ByteBuf a fresh
    // heap allocation, multiplying GC pressure under bursty packet traffic.
    // PooledAllocator + ignore the property entirely is the right default.
    System.out.println("CraftsMine is distributed under the Terms and Conditions of Creadores Program ©2024");
    System.out.println("Read the Terms and Conditions at: https://creadoresprogram.blogspot.com/p/craftsmine.html");
    Item.init();
    Attribute.init();
    new Server(DATA_PATH);
  }
}
