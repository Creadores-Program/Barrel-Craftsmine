package org.CreadoresProgram.CraftsMine.inventory;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
public class Item{
  public static final HashMap<Integer, Integer> ITEMS_AVIABLE = new HashMap<>();
  public static final HashMap<Integer, Integer> BLOCKS_AVIABLE = new HashMap<>();
  public static final List<Integer> ITEMS_BLOCKS_EQUALS = Arrays.asList(new Integer[]{
    0,
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10,
    11,
    12,
    13,
    14,
    15,
    16,
    17,
    18,
    19,
    20,
    21,
    22,
    23,
    24,
    25,
    26,
    27,
    28,
    29,
    30,
    31,
    32,
    33,
    34,
    35,
    37,
    38,
    39,
    40,
    41,
    42,
    43,
    44,
    45,
    46,
    47,
    48,
    49,
    50,
    51,
    52,
    53,
    54,
    55,
    56,
    57,
    58,
    59,
    60,
    61,
    62,
    63,
    64,
    65,
    66,
    67,
    68,
    69,
    70,
    71,
    72,
    73,
    74,
    75,
    76,
    77,
    78,
    79,
    80,
    81,
    82,
    83,
    85,
    86,
    87,
    88,
    89,
    90,
    91,
    92,
    93,
    94,
    95,
    96,
    98,
    99,
    100,
    101,
    102,
    103,
    104,
    105,
    106,
    107,
    108,
    109,
    110,
    111,
    112,
    113,
    114,
    116,
    117,
    118,
    119,
    120,
    121,
    123,
    124,
    126,
    128,
    129,
    131,
    133,
    134,
    135,
    136,
    139,
    140,
    141,
    142,
    143,
    144,
    145,
    146,
    147,
    148,
    149,
    150,
    151,
    152,
    153,
    154,
    155,
    156,
    157,
    158,
    159,
    161,
    162,
    163,
    164,
    165,
    167,
    170,
    171,
    172,
    173,
    174,
    175,
    178,
    179,
    180,
    181,
    182,
    183,
    184,
    185,
    186,
    187,
    193,
    194,
    195,
    196,
    197,
    198,
    199,
    242,
    243,
    244,
    245,
    246,
    247,
    248,
    249,
    251
    
  });
  public int realId = 0;
  public int realData = 0;
  public Object[] itemIns = new Object[4];
  public static void init(){
    //BLOCKS
    BLOCKS_AVIABLE.put(416, 95); //Barrier
    BLOCKS_AVIABLE.put(212, 95); //BORDER_BLOCK
    BLOCKS_AVIABLE.put(130, 54); //Ender Chest
    BLOCKS_AVIABLE.put(160, 102); //STAINED_GLASS_PANE
    BLOCKS_AVIABLE.put(190, 102); //HARD_GLASS_PANE
    BLOCKS_AVIABLE.put(191, 102); //HARD_STAINED_GLASS_PANE
    BLOCKS_AVIABLE.put(202, 50); //COLORED_TORCH_RG
    BLOCKS_AVIABLE.put(204, 50); //COLORED_TORCH_BP
    BLOCKS_AVIABLE.put(206, 121); //END_BRICKS
    BLOCKS_AVIABLE.put(207, 174); //FROSTED_ICE
    BLOCKS_AVIABLE.put(215, 112); //RED_NETHER_BRICK
    BLOCKS_AVIABLE.put(214, 87); //BLOCK_NETHER_WART_BLOCK
    BLOCKS_AVIABLE.put(253, 20); //HARD_GLASS
    BLOCKS_AVIABLE.put(254, 20); //HARD_STAINED_GLASS
    BLOCKS_AVIABLE.put(266, 174); //BLUE_ICE
    
    //ITEMS
//    ITEMS_AVIABLE.put(new Item(), new Item());
  }
  public Item(int id, int data, int cnt, byte[] nbt){
    this.itemIns[0] = id;
    this.realId = id;
    this.itemIns[1] = data /* & 0xffff */;
    this.realData = data;
    this.itemIns[2] = cnt;
    this.itemIns[3] = nbt;
  }
  public static Item translateItem(Item item){
    if(ITEMS_BLOCKS_EQUALS.contains((Integer) item.realId)){
      return item;
    }
    if(ITEMS_AVIABLE.get((Integer) item.realId) == null && BLOCKS_AVIABLE.get((Integer) item.realId) == null){
      return new Item(0, 0, 1, new byte[0]);
    }
    if(BLOCKS_AVIABLE.get((Integer) item.realId) != null){
      return new Item(BLOCKS_AVIABLE.get((Integer) item.realId), item.realData, (int) item.itemIns[2], (byte[]) item.itemIns[3]);
    }
    return new Item(ITEMS_AVIABLE.get((Integer) item.realId), item.realData, (int) item.itemIns[2], (byte[]) item.itemIns[3]);
  }
  public static Item translateBlock(Item block){
    if(ITEMS_BLOCKS_EQUALS.contains((Integer) block.realId)){
      return block;
    }
    if(BLOCKS_AVIABLE.get((Integer) block.realId) != null){
      return new Item(BLOCKS_AVIABLE.get((Integer) block.realId), block.realData, (int) block.itemIns[2], (byte[]) block.itemIns[3]);
    }
    return new Item(248, block.realData, (int) block.itemIns[2], (byte[]) block.itemIns[3]);
  }
}
