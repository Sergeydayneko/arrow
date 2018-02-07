package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(Function0::class)
interface Function0FunctorInstance : Functor<ForFunction0> {
    override fun <A, B> map(fa: Function0Kind<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.reify().map(f)
}

@instance(Function0::class)
interface Function0ApplicativeInstance : Applicative<ForFunction0> {
    override fun <A, B> ap(fa: Function0Kind<A>, ff: Function0Kind<kotlin.Function1<A, B>>): Function0<B> =
            fa.reify().ap(ff)

    override fun <A, B> map(fa: Function0Kind<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.reify().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)
}

@instance(Function0::class)
interface Function0MonadInstance : Monad<ForFunction0> {
    override fun <A, B> ap(fa: Function0Kind<A>, ff: Function0Kind<kotlin.Function1<A, B>>): Function0<B> =
            fa.reify().ap(ff)

    override fun <A, B> flatMap(fa: Function0Kind<A>, f: kotlin.Function1<A, Function0Kind<B>>): Function0<B> =
            fa.reify().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Kind<Either<A, B>>>): Function0<B> =
            Function0.tailRecM(a, f)

    override fun <A, B> map(fa: Function0Kind<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.reify().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)
}

@instance(Function0::class)
interface Function0ComonadInstance : Comonad<ForFunction0> {
    override fun <A, B> coflatMap(fa: Function0Kind<A>, f: kotlin.Function1<Function0Kind<A>, B>): Function0<B> =
            fa.reify().coflatMap(f)

    override fun <A> extract(fa: Function0Kind<A>): A =
            fa.reify().extract()

    override fun <A, B> map(fa: Function0Kind<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.reify().map(f)
}

@instance(Function0::class)
interface Function0BimonadInstance : Bimonad<ForFunction0> {
    override fun <A, B> ap(fa: Function0Kind<A>, ff: Function0Kind<kotlin.Function1<A, B>>): Function0<B> =
            fa.reify().ap(ff)

    override fun <A, B> flatMap(fa: Function0Kind<A>, f: kotlin.Function1<A, Function0Kind<B>>): Function0<B> =
            fa.reify().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Kind<Either<A, B>>>): Function0<B> =
            Function0.tailRecM(a, f)

    override fun <A, B> map(fa: Function0Kind<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.reify().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)

    override fun <A, B> coflatMap(fa: Function0Kind<A>, f: kotlin.Function1<Function0Kind<A>, B>): Function0<B> =
            fa.reify().coflatMap(f)

    override fun <A> extract(fa: Function0Kind<A>): A =
            fa.reify().extract()
}
