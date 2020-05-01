# Simulator_ISA

Overview of Our Architecture

Our Instruction Set Simulator architecture is based on a clean sheet design, and optimized to be as efficient for a  power-saving and mobile application. We wanted both the underlying code and the user interface to be easily understandable and organized.

Simulator Design and Implementation

Word Size and Data types: Our clean sheet design has a word size of 32 bits with big endian addressing and we support integer data types. We initially wanted to support more data types, but we ended up focusing our time on building an ISA that was reliable, even though the final product would not be as complex.

Registers: We have integer registers implemented in our ISA, and the contents of the register can be seen in real time in the UI. Integer registers have 12 general purpose registers for integer data and 4 which are reserved for a program counter and status/flags.

R0: Zero Register
R1: GP
...
R12: GP
R13: Program Counter
R14: Accumulator
Reserved

Instructions: Instructions in our ISA are-
Arithmetic and Logical: ADD, SUB, MULT, XOR, CMP for integer data types
Memory: LOAD and STORE for integer data types.
Control flow: CBNZ(Conditional Branch on Not Zero), CBZ(Conditional Branch on Zero), JNG(Jump on Negative)

Memory and Cache: We try to keep our memory small because mobile devices are memory constrained. Each line in the memory contains 8 words and there are 16 lines in memory. So we have 7 bit addresses and we have 2^7 words in our memory. It takes 100 cycles to access data in the memory. We use L1 direct-mapped cache which also contains 8 words in a line and there are 4 lines of memory. It takes 2 cycles to access data in the cache. We use the write-through scheme to write data when not present in the cache as it is safe and more reliable. The data in the cache is indexed using a tag and word.

Pipeline:  We use a RISC pipeline which is typically 5 stages(IF, ID, EX, MEM, WB). In the subsections below we describe in detail what each part of the pipeline does (functions of Class Pipeline). 
Instruction Fetch: In this stage, we get the program counter and use this to fetch the instruction from memory. This is passed to the next stage in the pipeline.
Instruction Decode: In this stage, we use the instruction to break into the following three operand instruction format (34 bit) and the number of bits each of the fields take- 

Opcode (4 bits)
Addressing mode for Result (3 bits)
Result (7 bits)
Addressing mode for Op1 (3 bits)
Op1 (7 bits)
Addressing mode for Op2 (3 bits)
Op2 (7 bits)

Below are the addressing modes and the indices representing them  for the different instructions.
0 - Memory Direct Addressing
1 - Register Direct Addressing
2 - Immediate Addressing
3 - PC relative addressing
4 - Register Indirect Addressing

Below are the instructions and their opcodes and their 4 bit opcode codes

1-0001-LOAD
2-0010-STORE
3-0011-ADD
4-0100-SUB
5-0101-CBNZ
6-0110-CBZ
7-0111-CMP
8-1000-JNG
9-1001-MULT
10-1010-XOR

Execute: In this stage, we take the instruction format array generated in the previous step and perform the operation to be performed based on the addressing mode and generates a result. 
Memory Access: In this step, we calculate the cycles taken by the instruction to perform any read or write from memory or cache. 
Write-Back: In this step, we write the result value generated in the previous steps to its respective address based on the addressing mode. 

We also have a controller program (main) that uses an object for each of the instructions in the machine program using a boolean array called completed to keep track of how many stages in the pipeline have been completed. Each of these objects also have their own mem_stall, data_stall to keep track of the number of cycles the pipeline should be halted. These stalls are calculated here based on memory stalls, structural and data hazards that might be encountered in the pipeline and returns no-op to the following instructions for that cycle. We also use a 2d global variable timesteps to keep track of each of the timestep and what stage each of the instructions are at including stalls. Memory, Cache and Registers are all static variables of the above class. Other than this, there are some helper functions for the pipeline functions.

Benchmarks
 
We have two benchmarks that we would like to show for the four modes (with/without cache, with/without pipeline) for integer data larger than the size of the cache. 
1. Sum of the first n natural numbers (prog1.txt)
2. Selection Sort (prog2.txt)

