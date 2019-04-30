

This repository contains code examples and theory about the Specialization Course in Scala from Ecole Polytechnique Federale de Lausanne "Functional Programming in Scala Specialization". Ecole Polytechnique Federale de Lausanne EPFL is the university where "Scala Language" is developed. The Specialization Course in Scala is divided in five modules:

    Principles of Functional Programming in Scala by Martin Ordesky. Martin Ordesky is the creator of the Scala language. He is a professor at EPFL in Lausanne, Switzerland, where since 2001 he has led the team that developed the Scala language libraries and compiler. The primary objective of the course is to learn functional programming from first principles. The course show us function programs, methods to construct them, and ways to reason about them. Functional programming just means programming without mutable variables, without assignments to those variables, without loops and the other imperative control structures. Functional programming also means focusing on the functions in the program. We can define a function anywhere including inside other functions. We can pass a function as a parameter to another function and return it, as a result from a function. Why Functional Programming? because it's very good for exploiting parallelism on multi call and cloud computing, so functional programming is important for parallelism and concurrency. Scala is a mix between Functional, Imperative and Object-Oriented programming paradigm. The course is full of quizzes, examples, exercises and assignments. The goals are:
        understand the principles of functional programming,
        write purely functional programs, using recursion, pattern matching, and higher-order functions,
        combine functional programming with objects and classes,
        design immutable data structures,
        reason about properties of functions,
        understand generic types for functional programs
        Recommended books: Structure and Interpretation of Computer Programs, by Harold Ibelson and Gerald Susman, second edition appeared at MIT Press in'96. Programming in Scala second edition, by Martiy Ordesky, Lex Spoon, and Bill Venners.

    Functional Program Design in Scala by Martin Ordesky. In this course we will learn how to apply the functional programming style in the design of larger applications. We'll get to know important new functional programming concepts, from lazy evaluation to structuring libraries using monads. We'll work on larger and more involved examples, from state space exploration to random testing to discrete circuit simulators. We’ll also learn some best practices on how to write good Scala code in the real world. Several parts of this course deal with the question how functional programming interacts with mutable state. We will explore the consequences of combining functions and state. We will also look at purely functional alternatives to mutable state, using infinite data structures or functional reactive programming. Learning Outcomes:
        recognize and apply design principles of functional programs,
        design functional libraries and their APIs,
        competently combine functions and state in one program,
        understand reasoning techniques for programs that combine functions and state,
        write simple functional reactive applications.

    Parallel Programming in Scala by Dr. Aleksandar Prokopec, Principal Researcher and Prof. Viktor Kuncak, Associate Professor at Ecole Polytechnique Federale de Lausanne. In this course, you will learn the basics of parallel computing, both from a theoretical and a practical aspect. You will see how many familiar ideas from functional programming map perfectly to the data parallel paradigm. Concretely, you will learn about paralleling known algorithms such as merge sort or Monte Carlo methods. You will also discover some new algorithms, like parallel reduction, or parallel perfect sum. This knowledge applies to practical problems. Graphics processing, parsing or particle simulation. These are all algorithms that you will implement in Scala using programming primitives from the standard library. Our lectures contrast parallel programming with sequential programming in Scala that you're already familiar with. We explore conditions that make a parallel program compute the same result as its sequential counterpart.
    In terms of performance, you will learn how to estimate it analytically, as well as how to measure it for your implementations. Throughout, we'll apply these concepts through several hands-on examples that analyze real-world data, such as popular
    algorithms like k-means clustering.
        reason about task and data parallel programs,
        express common algorithms in a functional style and solve them in parallel,
        competently microbenchmark parallel code,
        write programs that effectively use parallel collections to achieve performance

    Big Data Analysis with Scala & Spark by Heather Miller, research scientist at Ecole Polytechnique Federale de Lausanne. In this course we're going to cover Spark's programming model in depth. We're then going to go into distributing computation, how to do it and how the cluster is actually laid out in Spark? We're then going to move on and spend quite a bit of time learning about how to improve performance in Spark. Looking at stuff
    like data locality and how to avoid recomputation and especially data shuffles in Spark. We're going to spend quite a bit of time trying to understand when data shuffles will occur. And finally, we're going to spend quite a bit of time diving into the relational operations available in Spark SQL module. To learn how to use the relational operations on DataFrames and Datasets and also all of the benefits that they bring you, as well as a handful of limitations.
        read data from persistent storage and load it into Apache Spark,
        manipulate data with Spark and Scala,
        express algorithms for data analysis in a functional style,
        recognize how to avoid shuffles and recomputation in Spark,
        RDDs, PairRDDs, Spark SQL, DataFrames, Datasets

    Functional Programming in Scala Capstone In the final capstone project you will apply the skills you learned by building a large data-intensive application using real-world data.

    You will implement a complete application processing several gigabytes of data. This application will show interactive visualizations of the evolution of temperatures over time all over the world.

    The development of such an application will involve: — transforming data provided by weather stations into meaningful information like, for instance, the average temperature of each point of the globe over the last ten years ; — then, making images from this information by using spatial and linear interpolation techniques ; — finally, implementing how the user interface will react to users’ actions.

